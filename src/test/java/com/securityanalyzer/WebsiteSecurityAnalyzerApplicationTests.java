package com.securityanalyzer;

import com.securityanalyzer.dto.CreateScanRequestDto;
import com.securityanalyzer.dto.ScanHistoryDto;
import com.securityanalyzer.service.ScanHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WebsiteSecurityAnalyzerApplicationTests {

    @Autowired
    private ScanHistoryService scanHistoryService;

    @Test
    void contextLoads() {
        assertNotNull(scanHistoryService, "ScanHistoryService should be loaded in spring context");
    }

    @Test
    void testScanHistoryCrudOperations() {
        // Create scan
        CreateScanRequestDto request = CreateScanRequestDto.builder()
                .targetUrl("https://google.com")
                .build();
        ScanHistoryDto createdScan = scanHistoryService.createScanPlaceholder(request);
        
        assertNotNull(createdScan.getId());
        assertEquals("https://google.com", createdScan.getTargetUrl());
        assertEquals("IN_PROGRESS", createdScan.getStatus());

        // Get single scan
        ScanHistoryDto retrievedScan = scanHistoryService.getScanById(createdScan.getId());
        assertEquals(createdScan.getId(), retrievedScan.getId());

        // Update status
        ScanHistoryDto updatedScan = scanHistoryService.updateScanStatus(createdScan.getId(), "COMPLETED", 95);
        assertEquals("COMPLETED", updatedScan.getStatus());
        assertEquals(95, updatedScan.getScore());
        assertNotNull(updatedScan.getCompletedAt());

        // Get all scans
        List<ScanHistoryDto> allScans = scanHistoryService.getAllScans();
        assertFalse(allScans.isEmpty());

        // Delete scan
        scanHistoryService.deleteScan(createdScan.getId());
        assertThrows(IllegalArgumentException.class, () -> scanHistoryService.getScanById(createdScan.getId()));
    }

    @Test
    void testUrlValidation() {
        assertTrue(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl("https://google.com"));
        assertTrue(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl("http://example.org/path?q=1"));
        assertFalse(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl("ftp://invalid.com"));
        assertFalse(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl("not-a-url"));
        assertFalse(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl(null));
        
        // SSRF blocks
        assertFalse(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl("http://localhost"));
        assertFalse(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl("http://127.0.0.1"));
        assertFalse(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl("https://192.168.1.100"));
        assertFalse(com.securityanalyzer.util.SecurityScannerUtil.isValidUrl("http://10.0.0.1/admin"));
    }

    @Autowired
    private com.securityanalyzer.service.scanner.scoring.SecurityScoringEngine securityScoringEngine;

    @Test
    void testScoringEngineIntegration() {
        assertNotNull(securityScoringEngine, "SecurityScoringEngine should be loaded in spring context");
        
        // Construct a mock ScanHistory
        com.securityanalyzer.entity.ScanHistory scan = com.securityanalyzer.entity.ScanHistory.builder()
                .targetUrl("https://example.com")
                .responseTimeMs(250L)
                .redirectCount(0)
                .build();
                
        // Add secure headers
        scan.addSecurityHeader(com.securityanalyzer.entity.SecurityHeader.builder().headerName("Content-Security-Policy").isPresent(true).securityRating("SECURE").build());
        scan.addSecurityHeader(com.securityanalyzer.entity.SecurityHeader.builder().headerName("Strict-Transport-Security").isPresent(true).securityRating("SECURE").build());
        scan.addSecurityHeader(com.securityanalyzer.entity.SecurityHeader.builder().headerName("X-Frame-Options").isPresent(true).securityRating("SECURE").build());
        scan.addSecurityHeader(com.securityanalyzer.entity.SecurityHeader.builder().headerName("X-Content-Type-Options").isPresent(true).securityRating("SECURE").build());
        scan.addSecurityHeader(com.securityanalyzer.entity.SecurityHeader.builder().headerName("Referrer-Policy").isPresent(true).securityRating("SECURE").build());
        
        // Add valid SSL
        scan.setSslInfo(com.securityanalyzer.entity.SSLInfo.builder().isSslEnabled(true).isExpired(false).build());
        
        // Add secure cookie
        scan.addCookieAnalysis(com.securityanalyzer.entity.CookieAnalysis.builder().cookieName("session").isSecure(true).isHttpOnly(true).build());

        int score = securityScoringEngine.score(scan);
        
        // HTTPS = 20
        // SSL = 20
        // Headers = 30
        // Cookies = 15
        // Redirects = 10
        // Response time = 5
        // Total = 100
        assertEquals(100, score, "Secure website should get a perfect score of 100");
        assertTrue(scan.getRecommendations().isEmpty(), "Perfect scan should have no recommendations");
    }

    @Test
    void testScoringEngineWithFailedChecks() {
        // Construct an insecure ScanHistory
        com.securityanalyzer.entity.ScanHistory scan = com.securityanalyzer.entity.ScanHistory.builder()
                .targetUrl("http://insecure-site.com")
                .responseTimeMs(2500L) // Slow
                .redirectCount(5) // Excessive
                .build();
                
        // No headers, no SSL info, insecure cookies (deducts 3 * 2 = 6 points)
        scan.addCookieAnalysis(com.securityanalyzer.entity.CookieAnalysis.builder().cookieName("session").isSecure(false).isHttpOnly(false).build());

        int score = securityScoringEngine.score(scan);
        
        // HTTPS = 0
        // SSL = 0
        // Headers = 0
        // Cookies = 9 (15 - 6 = 9)
        // Redirects = 5 (>= 4 redirects gives 5 points)
        // Response time = 0 (>= 2000ms gives 0 points)
        // Total = 14
        assertEquals(14, score);
        assertFalse(scan.getRecommendations().isEmpty(), "Failed checks should generate recommendations");
        
        boolean hasHttpsRec = scan.getRecommendations().stream().anyMatch(r -> "HTTPS".equals(r.getCategory()));
        boolean hasSslRec = scan.getRecommendations().stream().anyMatch(r -> "SSL".equals(r.getCategory()));
        boolean hasHeaderRec = scan.getRecommendations().stream().anyMatch(r -> "HEADERS".equals(r.getCategory()));
        boolean hasCookieRec = scan.getRecommendations().stream().anyMatch(r -> "COOKIES".equals(r.getCategory()));
        boolean hasRedirectRec = scan.getRecommendations().stream().anyMatch(r -> "REDIRECTS".equals(r.getCategory()));
        boolean hasPerfRec = scan.getRecommendations().stream().anyMatch(r -> "PERFORMANCE".equals(r.getCategory()));
        
        assertTrue(hasHttpsRec, "Should recommend HTTPS");
        assertTrue(hasSslRec, "Should recommend SSL");
        assertTrue(hasHeaderRec, "Should recommend Headers");
        assertTrue(hasCookieRec, "Should recommend Cookie flags");
        assertTrue(hasRedirectRec, "Should recommend Redirect fix");
        assertTrue(hasPerfRec, "Should recommend Performance improvement");
    }
}
