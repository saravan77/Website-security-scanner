package com.securityanalyzer.service;

import com.securityanalyzer.dto.CreateScanRequestDto;
import com.securityanalyzer.dto.ScanHistoryDto;
import com.securityanalyzer.entity.CookieAnalysis;
import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.SecurityHeader;
import com.securityanalyzer.entity.SSLInfo;
import com.securityanalyzer.entity.ScanRecommendation;
import com.securityanalyzer.repository.ScanHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScanHistoryServiceImpl implements ScanHistoryService {

    private static final Logger log = LoggerFactory.getLogger(ScanHistoryServiceImpl.class);

    private final ScanHistoryRepository scanHistoryRepository;
    private final com.securityanalyzer.service.scanner.WebsiteScannerService websiteScannerService;

    @Autowired
    public ScanHistoryServiceImpl(ScanHistoryRepository scanHistoryRepository,
                                  com.securityanalyzer.service.scanner.WebsiteScannerService websiteScannerService) {
        this.scanHistoryRepository = scanHistoryRepository;
        this.websiteScannerService = websiteScannerService;
    }

    @Override
    public ScanHistoryDto createScanPlaceholder(CreateScanRequestDto request) {
        log.info("Creating scan history placeholder for target URL: {}", request.getTargetUrl());
        
        ScanHistory scan = ScanHistory.builder()
                .targetUrl(request.getTargetUrl())
                .status("IN_PROGRESS")
                .score(0)
                .build();

        ScanHistory savedScan = scanHistoryRepository.save(scan);
        log.debug("Successfully saved scan placeholder with ID: {}", savedScan.getId());
        return convertToDto(savedScan);
    }

    @Override
    public ScanHistoryDto executeScan(CreateScanRequestDto request) {
        log.info("Executing security scan for target URL: {}", request.getTargetUrl());
        
        // 1. Create and save initial ScanHistory placeholder
        ScanHistory scan = ScanHistory.builder()
                .targetUrl(request.getTargetUrl())
                .status("IN_PROGRESS")
                .score(0)
                .build();
        ScanHistory savedScan = scanHistoryRepository.save(scan);
        
        // 2. Run the scan engine (updates the scan history details and saves to DB)
        ScanHistory completedScan = websiteScannerService.runScan(savedScan);
        
        return convertToDto(completedScan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScanHistoryDto> getAllScans() {
        log.info("Retrieving all scan history records");
        return scanHistoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScanHistoryDto getScanById(Long id) {
        log.info("Retrieving scan history with ID: {}", id);
        ScanHistory scan = scanHistoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Scan history not found for ID: {}", id);
                    return new IllegalArgumentException("Scan history not found for ID: " + id);
                });
        return convertToDto(scan);
    }

    @Override
    public void deleteScan(Long id) {
        log.info("Deleting scan history record with ID: {}", id);
        if (!scanHistoryRepository.existsById(id)) {
            log.error("Failed to delete. Scan history not found for ID: {}", id);
            throw new IllegalArgumentException("Scan history not found for ID: " + id);
        }
        scanHistoryRepository.deleteById(id);
        log.debug("Successfully deleted scan history with ID: {}", id);
    }

    @Override
    public ScanHistoryDto updateScanStatus(Long id, String status, int score) {
        log.info("Updating status of scan ID: {} to {} with score: {}", id, status, score);
        ScanHistory scan = scanHistoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Scan history not found for update, ID: {}", id);
                    return new IllegalArgumentException("Scan history not found for ID: " + id);
                });
        
        scan.setStatus(status);
        scan.setScore(score);
        if ("COMPLETED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)) {
            scan.setCompletedAt(LocalDateTime.now());
        }
        
        ScanHistory updatedScan = scanHistoryRepository.save(scan);
        log.debug("Successfully updated scan ID: {}", updatedScan.getId());
        return convertToDto(updatedScan);
    }

    private ScanHistoryDto convertToDto(ScanHistory scan) {
        if (scan == null) return null;

        List<ScanHistoryDto.SecurityHeaderDto> headers = scan.getSecurityHeaders() == null ? null :
                scan.getSecurityHeaders().stream()
                        .map(this::convertHeaderToDto)
                        .collect(Collectors.toList());

        List<ScanHistoryDto.CookieAnalysisDto> cookies = scan.getCookieAnalyses() == null ? null :
                scan.getCookieAnalyses().stream()
                        .map(this::convertCookieToDto)
                        .collect(Collectors.toList());

        List<ScanHistoryDto.ScanRecommendationDto> recommendations = scan.getRecommendations() == null ? null :
                scan.getRecommendations().stream()
                        .map(this::convertRecommendationToDto)
                        .collect(Collectors.toList());

        ScanHistoryDto.SSLInfoDto ssl = convertSslToDto(scan.getSslInfo());

        return ScanHistoryDto.builder()
                .id(scan.getId())
                .targetUrl(scan.getTargetUrl())
                .status(scan.getStatus())
                .score(scan.getScore())
                .responseTimeMs(scan.getResponseTimeMs())
                .redirectCount(scan.getRedirectCount())
                .createdAt(scan.getCreatedAt())
                .completedAt(scan.getCompletedAt())
                .securityHeaders(headers)
                .cookieAnalyses(cookies)
                .recommendations(recommendations)
                .sslInfo(ssl)
                .build();
    }

    private ScanHistoryDto.ScanRecommendationDto convertRecommendationToDto(ScanRecommendation rec) {
        if (rec == null) return null;
        return ScanHistoryDto.ScanRecommendationDto.builder()
                .id(rec.getId())
                .category(rec.getCategory())
                .checkName(rec.getCheckName())
                .recommendation(rec.getRecommendation())
                .build();
    }

    private ScanHistoryDto.SecurityHeaderDto convertHeaderToDto(SecurityHeader header) {
        if (header == null) return null;
        return ScanHistoryDto.SecurityHeaderDto.builder()
                .id(header.getId())
                .headerName(header.getHeaderName())
                .headerValue(header.getHeaderValue())
                .isPresent(header.isPresent())
                .securityRating(header.getSecurityRating())
                .recommendation(header.getRecommendation())
                .build();
    }

    private ScanHistoryDto.CookieAnalysisDto convertCookieToDto(CookieAnalysis cookie) {
        if (cookie == null) return null;
        return ScanHistoryDto.CookieAnalysisDto.builder()
                .id(cookie.getId())
                .cookieName(cookie.getCookieName())
                .isSecure(cookie.isSecure())
                .isHttpOnly(cookie.isHttpOnly())
                .sameSite(cookie.getSameSite())
                .isExpired(cookie.isExpired())
                .build();
    }

    private ScanHistoryDto.SSLInfoDto convertSslToDto(SSLInfo ssl) {
        if (ssl == null) return null;
        return ScanHistoryDto.SSLInfoDto.builder()
                .id(ssl.getId())
                .isSslEnabled(ssl.isSslEnabled())
                .protocol(ssl.getProtocol())
                .cipherSuite(ssl.getCipherSuite())
                .issuer(ssl.getIssuer())
                .validFrom(ssl.getValidFrom())
                .validTo(ssl.getValidTo())
                .isExpired(ssl.isExpired())
                .build();
    }
}
