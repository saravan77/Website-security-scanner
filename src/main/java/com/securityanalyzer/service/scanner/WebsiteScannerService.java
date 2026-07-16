package com.securityanalyzer.service.scanner;

import com.securityanalyzer.config.ScanConfig;
import com.securityanalyzer.entity.CookieAnalysis;
import com.securityanalyzer.entity.SSLInfo;
import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.SecurityHeader;
import com.securityanalyzer.repository.ScanHistoryRepository;
import com.securityanalyzer.service.scanner.scoring.SecurityScoringEngine;
import com.securityanalyzer.util.SecurityScannerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WebsiteScannerService {

    private static final Logger log = LoggerFactory.getLogger(WebsiteScannerService.class);

    private final SslInspector sslInspector;
    private final HttpScanner httpScanner;
    private final SecurityHeaderAnalyzer securityHeaderAnalyzer;
    private final CookieAnalyzer cookieAnalyzer;
    private final ScanHistoryRepository scanHistoryRepository;
    private final SecurityScoringEngine securityScoringEngine;
    private final ScanConfig scanConfig;

    @Autowired
    public WebsiteScannerService(
            SslInspector sslInspector,
            HttpScanner httpScanner,
            SecurityHeaderAnalyzer securityHeaderAnalyzer,
            CookieAnalyzer cookieAnalyzer,
            ScanHistoryRepository scanHistoryRepository,
            SecurityScoringEngine securityScoringEngine,
            ScanConfig scanConfig) {
        this.sslInspector = sslInspector;
        this.httpScanner = httpScanner;
        this.securityHeaderAnalyzer = securityHeaderAnalyzer;
        this.cookieAnalyzer = cookieAnalyzer;
        this.scanHistoryRepository = scanHistoryRepository;
        this.securityScoringEngine = securityScoringEngine;
        this.scanConfig = scanConfig;
    }

    public ScanHistory runScan(ScanHistory scan) {
        String url = scan.getTargetUrl();
        log.info("Starting security scan for target: {}", url);

        if (!SecurityScannerUtil.isValidUrl(url)) {
            log.error("Invalid target URL provided: {}", url);
            scan.setStatus("FAILED");
            scan.setScore(0);
            scan.setCompletedAt(LocalDateTime.now());
            return scanHistoryRepository.save(scan);
        }

        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String scheme = uri.getScheme();
            int port = uri.getPort();
            if (port == -1) {
                port = "https".equalsIgnoreCase(scheme) ? 443 : 80;
            }

            // 1. SSL Inspection (only if scheme is https or port is 443)
            SSLInfo sslInfo = null;
            if ("https".equalsIgnoreCase(scheme) || port == 443) {
                sslInfo = sslInspector.inspect(host, port, scanConfig.getConnectTimeoutMs());
            } else {
                sslInfo = SSLInfo.builder()
                        .isSslEnabled(false)
                        .build();
            }

            // 2. HTTP Scanning
            HttpScanner.HttpScanResult httpResult = httpScanner.scan(url, scanConfig.getConnectTimeoutMs());

            scan.setResponseTimeMs(httpResult.getResponseTimeMs());
            scan.setRedirectCount(httpResult.getRedirectCount());

            if (!httpResult.isAvailable()) {
                log.warn("Target URL {} is unavailable. Failing scan.", url);
                scan.setStatus("FAILED");
                scan.setScore(0);
                scan.setCompletedAt(LocalDateTime.now());
                if (sslInfo != null) {
                    scan.setSslInfo(sslInfo);
                }
                return scanHistoryRepository.save(scan);
            }

            // 3. Security Headers Analysis
            List<SecurityHeader> headers = securityHeaderAnalyzer.analyze(httpResult.getHeaders());

            // 4. Cookies Analysis
            List<CookieAnalysis> cookies = cookieAnalyzer.analyze(httpResult.getSetCookieHeaders());

            // Associate child entities with the main scan before scoring
            scan.setSslInfo(sslInfo);
            for (SecurityHeader h : headers) {
                scan.addSecurityHeader(h);
            }
            for (CookieAnalysis c : cookies) {
                scan.addCookieAnalysis(c);
            }

            // 5. Score Calculation via Modular Scoring Engine (also appends recommendations)
            int score = securityScoringEngine.score(scan);

            // Populate scan history record completion details
            scan.setStatus("COMPLETED");
            scan.setScore(score);
            scan.setCompletedAt(LocalDateTime.now());

            log.info("Successfully completed security scan for {}. Score: {}", url, score);
            return scanHistoryRepository.save(scan);

        } catch (Exception e) {
            log.error("Unexpected error executing scan for {}: {}", url, e.getMessage(), e);
            scan.setStatus("FAILED");
            scan.setScore(0);
            scan.setCompletedAt(LocalDateTime.now());
            return scanHistoryRepository.save(scan);
        }
    }
}
