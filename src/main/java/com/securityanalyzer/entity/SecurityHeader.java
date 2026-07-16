package com.securityanalyzer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "security_headers")
public class SecurityHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String headerName;

    @Column(columnDefinition = "TEXT")
    private String headerValue;

    private boolean isPresent;

    private String securityRating; // E.g., SECURE, WARNING, DANGER, NONE

    private String recommendation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_history_id", nullable = false)
    private ScanHistory scanHistory;

    public SecurityHeader() {}

    public SecurityHeader(Long id, String headerName, String headerValue, boolean isPresent, String securityRating, 
                          String recommendation, ScanHistory scanHistory) {
        this.id = id;
        this.headerName = headerName;
        this.headerValue = headerValue;
        this.isPresent = isPresent;
        this.securityRating = securityRating;
        this.recommendation = recommendation;
        this.scanHistory = scanHistory;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHeaderName() { return headerName; }
    public void setHeaderName(String headerName) { this.headerName = headerName; }

    public String getHeaderValue() { return headerValue; }
    public void setHeaderValue(String headerValue) { this.headerValue = headerValue; }

    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }

    public String getSecurityRating() { return securityRating; }
    public void setSecurityRating(String securityRating) { this.securityRating = securityRating; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public ScanHistory getScanHistory() { return scanHistory; }
    public void setScanHistory(ScanHistory scanHistory) { this.scanHistory = scanHistory; }

    public static SecurityHeaderBuilder builder() {
        return new SecurityHeaderBuilder();
    }

    public static class SecurityHeaderBuilder {
        private Long id;
        private String headerName;
        private String headerValue;
        private boolean isPresent;
        private String securityRating;
        private String recommendation;
        private ScanHistory scanHistory;

        public SecurityHeaderBuilder id(Long id) { this.id = id; return this; }
        public SecurityHeaderBuilder headerName(String headerName) { this.headerName = headerName; return this; }
        public SecurityHeaderBuilder headerValue(String headerValue) { this.headerValue = headerValue; return this; }
        public SecurityHeaderBuilder isPresent(boolean isPresent) { this.isPresent = isPresent; return this; }
        public SecurityHeaderBuilder securityRating(String securityRating) { this.securityRating = securityRating; return this; }
        public SecurityHeaderBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public SecurityHeaderBuilder scanHistory(ScanHistory scanHistory) { this.scanHistory = scanHistory; return this; }

        public SecurityHeader build() {
            return new SecurityHeader(id, headerName, headerValue, isPresent, securityRating, recommendation, scanHistory);
        }
    }
}
