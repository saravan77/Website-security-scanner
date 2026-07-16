package com.securityanalyzer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cookie_analyses")
public class CookieAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cookieName;

    private boolean isSecure;

    private boolean isHttpOnly;

    private String sameSite; // E.g., Lax, Strict, None

    private boolean isExpired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_history_id", nullable = false)
    private ScanHistory scanHistory;

    public CookieAnalysis() {}

    public CookieAnalysis(Long id, String cookieName, boolean isSecure, boolean isHttpOnly, String sameSite, 
                          boolean isExpired, ScanHistory scanHistory) {
        this.id = id;
        this.cookieName = cookieName;
        this.isSecure = isSecure;
        this.isHttpOnly = isHttpOnly;
        this.sameSite = sameSite;
        this.isExpired = isExpired;
        this.scanHistory = scanHistory;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCookieName() { return cookieName; }
    public void setCookieName(String cookieName) { this.cookieName = cookieName; }

    public boolean isSecure() { return isSecure; }
    public void setSecure(boolean secure) { isSecure = secure; }

    public boolean isHttpOnly() { return isHttpOnly; }
    public void setHttpOnly(boolean httpOnly) { isHttpOnly = httpOnly; }

    public String getSameSite() { return sameSite; }
    public void setSameSite(String sameSite) { this.sameSite = sameSite; }

    public boolean isExpired() { return isExpired; }
    public void setExpired(boolean expired) { isExpired = expired; }

    public ScanHistory getScanHistory() { return scanHistory; }
    public void setScanHistory(ScanHistory scanHistory) { this.scanHistory = scanHistory; }

    public static CookieAnalysisBuilder builder() {
        return new CookieAnalysisBuilder();
    }

    public static class CookieAnalysisBuilder {
        private Long id;
        private String cookieName;
        private boolean isSecure;
        private boolean isHttpOnly;
        private String sameSite;
        private boolean isExpired;
        private ScanHistory scanHistory;

        public CookieAnalysisBuilder id(Long id) { this.id = id; return this; }
        public CookieAnalysisBuilder cookieName(String cookieName) { this.cookieName = cookieName; return this; }
        public CookieAnalysisBuilder isSecure(boolean isSecure) { this.isSecure = isSecure; return this; }
        public CookieAnalysisBuilder isHttpOnly(boolean isHttpOnly) { this.isHttpOnly = isHttpOnly; return this; }
        public CookieAnalysisBuilder sameSite(String sameSite) { this.sameSite = sameSite; return this; }
        public CookieAnalysisBuilder isExpired(boolean isExpired) { this.isExpired = isExpired; return this; }
        public CookieAnalysisBuilder scanHistory(ScanHistory scanHistory) { this.scanHistory = scanHistory; return this; }

        public CookieAnalysis build() {
            return new CookieAnalysis(id, cookieName, isSecure, isHttpOnly, sameSite, isExpired, scanHistory);
        }
    }
}
