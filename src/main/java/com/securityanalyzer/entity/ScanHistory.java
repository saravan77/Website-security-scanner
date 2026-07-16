package com.securityanalyzer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scan_histories")
public class ScanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetUrl;

    @Column(nullable = false)
    private String status; // E.g., IN_PROGRESS, COMPLETED, FAILED

    private int score; // E.g., 0-100 overall safety score

    private Long responseTimeMs;

    private int redirectCount;

    @OneToMany(mappedBy = "scanHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecurityHeader> securityHeaders = new ArrayList<>();

    @OneToMany(mappedBy = "scanHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CookieAnalysis> cookieAnalyses = new ArrayList<>();

    @OneToMany(mappedBy = "scanHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScanRecommendation> recommendations = new ArrayList<>();

    @OneToOne(mappedBy = "scanHistory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SSLInfo sslInfo;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public ScanHistory() {}

    public ScanHistory(Long id, String targetUrl, String status, int score, Long responseTimeMs, int redirectCount, 
                       List<SecurityHeader> securityHeaders, List<CookieAnalysis> cookieAnalyses, 
                       List<ScanRecommendation> recommendations, SSLInfo sslInfo, 
                       LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.targetUrl = targetUrl;
        this.status = status;
        this.score = score;
        this.responseTimeMs = responseTimeMs;
        this.redirectCount = redirectCount;
        this.securityHeaders = securityHeaders != null ? securityHeaders : new ArrayList<>();
        this.cookieAnalyses = cookieAnalyses != null ? cookieAnalyses : new ArrayList<>();
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
        setSslInfo(sslInfo);
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public int getRedirectCount() { return redirectCount; }
    public void setRedirectCount(int redirectCount) { this.redirectCount = redirectCount; }

    public List<SecurityHeader> getSecurityHeaders() { return securityHeaders; }
    public void setSecurityHeaders(List<SecurityHeader> securityHeaders) { this.securityHeaders = securityHeaders; }

    public List<CookieAnalysis> getCookieAnalyses() { return cookieAnalyses; }
    public void setCookieAnalyses(List<CookieAnalysis> cookieAnalyses) { this.cookieAnalyses = cookieAnalyses; }

    public List<ScanRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<ScanRecommendation> recommendations) { this.recommendations = recommendations; }

    public SSLInfo getSslInfo() { return sslInfo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    // Helper methods to maintain bidirectional integrity
    public void addSecurityHeader(SecurityHeader header) {
        securityHeaders.add(header);
        header.setScanHistory(this);
    }

    public void removeSecurityHeader(SecurityHeader header) {
        securityHeaders.remove(header);
        header.setScanHistory(null);
    }

    public void addCookieAnalysis(CookieAnalysis cookie) {
        cookieAnalyses.add(cookie);
        cookie.setScanHistory(this);
    }

    public void removeCookieAnalysis(CookieAnalysis cookie) {
        cookieAnalyses.remove(cookie);
        cookie.setScanHistory(null);
    }

    public void addRecommendation(ScanRecommendation rec) {
        recommendations.add(rec);
        rec.setScanHistory(this);
    }

    public void removeRecommendation(ScanRecommendation rec) {
        recommendations.remove(rec);
        rec.setScanHistory(null);
    }

    public void setSslInfo(SSLInfo sslInfo) {
        if (sslInfo == null) {
            if (this.sslInfo != null) {
                this.sslInfo.setScanHistory(null);
            }
        } else {
            sslInfo.setScanHistory(this);
        }
        this.sslInfo = sslInfo;
    }

    public static ScanHistoryBuilder builder() {
        return new ScanHistoryBuilder();
    }

    public static class ScanHistoryBuilder {
        private Long id;
        private String targetUrl;
        private String status;
        private int score;
        private Long responseTimeMs;
        private int redirectCount;
        private List<SecurityHeader> securityHeaders = new ArrayList<>();
        private List<CookieAnalysis> cookieAnalyses = new ArrayList<>();
        private List<ScanRecommendation> recommendations = new ArrayList<>();
        private SSLInfo sslInfo;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;

        public ScanHistoryBuilder id(Long id) { this.id = id; return this; }
        public ScanHistoryBuilder targetUrl(String targetUrl) { this.targetUrl = targetUrl; return this; }
        public ScanHistoryBuilder status(String status) { this.status = status; return this; }
        public ScanHistoryBuilder score(int score) { this.score = score; return this; }
        public ScanHistoryBuilder responseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; return this; }
        public ScanHistoryBuilder redirectCount(int redirectCount) { this.redirectCount = redirectCount; return this; }
        public ScanHistoryBuilder securityHeaders(List<SecurityHeader> securityHeaders) { this.securityHeaders = securityHeaders; return this; }
        public ScanHistoryBuilder cookieAnalyses(List<CookieAnalysis> cookieAnalyses) { this.cookieAnalyses = cookieAnalyses; return this; }
        public ScanHistoryBuilder recommendations(List<ScanRecommendation> recommendations) { this.recommendations = recommendations; return this; }
        public ScanHistoryBuilder sslInfo(SSLInfo sslInfo) { this.sslInfo = sslInfo; return this; }
        public ScanHistoryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ScanHistoryBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }

        public ScanHistory build() {
            return new ScanHistory(id, targetUrl, status, score, responseTimeMs, redirectCount, securityHeaders, cookieAnalyses, recommendations, sslInfo, createdAt, completedAt);
        }
    }
}
