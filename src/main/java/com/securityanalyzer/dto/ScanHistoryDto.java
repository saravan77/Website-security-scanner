package com.securityanalyzer.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ScanHistoryDto {

    private Long id;
    private String targetUrl;
    private String status;
    private int score;
    private Long responseTimeMs;
    private int redirectCount;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<SecurityHeaderDto> securityHeaders;
    private List<CookieAnalysisDto> cookieAnalyses;
    private List<ScanRecommendationDto> recommendations;
    private SSLInfoDto sslInfo;

    public ScanHistoryDto() {}

    public ScanHistoryDto(Long id, String targetUrl, String status, int score, Long responseTimeMs, int redirectCount, 
                          LocalDateTime createdAt, LocalDateTime completedAt, List<SecurityHeaderDto> securityHeaders, 
                          List<CookieAnalysisDto> cookieAnalyses, List<ScanRecommendationDto> recommendations, SSLInfoDto sslInfo) {
        this.id = id;
        this.targetUrl = targetUrl;
        this.status = status;
        this.score = score;
        this.responseTimeMs = responseTimeMs;
        this.redirectCount = redirectCount;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.securityHeaders = securityHeaders;
        this.cookieAnalyses = cookieAnalyses;
        this.recommendations = recommendations;
        this.sslInfo = sslInfo;
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<SecurityHeaderDto> getSecurityHeaders() { return securityHeaders; }
    public void setSecurityHeaders(List<SecurityHeaderDto> securityHeaders) { this.securityHeaders = securityHeaders; }

    public List<CookieAnalysisDto> getCookieAnalyses() { return cookieAnalyses; }
    public void setCookieAnalyses(List<CookieAnalysisDto> cookieAnalyses) { this.cookieAnalyses = cookieAnalyses; }

    public List<ScanRecommendationDto> getRecommendations() { return recommendations; }
    public void setRecommendations(List<ScanRecommendationDto> recommendations) { this.recommendations = recommendations; }

    public SSLInfoDto getSslInfo() { return sslInfo; }
    public void setSslInfo(SSLInfoDto sslInfo) { this.sslInfo = sslInfo; }

    public static ScanHistoryDtoBuilder builder() {
        return new ScanHistoryDtoBuilder();
    }

    public static class ScanHistoryDtoBuilder {
        private Long id;
        private String targetUrl;
        private String status;
        private int score;
        private Long responseTimeMs;
        private int redirectCount;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private List<SecurityHeaderDto> securityHeaders;
        private List<CookieAnalysisDto> cookieAnalyses;
        private List<ScanRecommendationDto> recommendations;
        private SSLInfoDto sslInfo;

        public ScanHistoryDtoBuilder id(Long id) { this.id = id; return this; }
        public ScanHistoryDtoBuilder targetUrl(String targetUrl) { this.targetUrl = targetUrl; return this; }
        public ScanHistoryDtoBuilder status(String status) { this.status = status; return this; }
        public ScanHistoryDtoBuilder score(int score) { this.score = score; return this; }
        public ScanHistoryDtoBuilder responseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; return this; }
        public ScanHistoryDtoBuilder redirectCount(int redirectCount) { this.redirectCount = redirectCount; return this; }
        public ScanHistoryDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ScanHistoryDtoBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public ScanHistoryDtoBuilder securityHeaders(List<SecurityHeaderDto> securityHeaders) { this.securityHeaders = securityHeaders; return this; }
        public ScanHistoryDtoBuilder cookieAnalyses(List<CookieAnalysisDto> cookieAnalyses) { this.cookieAnalyses = cookieAnalyses; return this; }
        public ScanHistoryDtoBuilder recommendations(List<ScanRecommendationDto> recommendations) { this.recommendations = recommendations; return this; }
        public ScanHistoryDtoBuilder sslInfo(SSLInfoDto sslInfo) { this.sslInfo = sslInfo; return this; }

        public ScanHistoryDto build() {
            return new ScanHistoryDto(id, targetUrl, status, score, responseTimeMs, redirectCount, createdAt, completedAt, securityHeaders, cookieAnalyses, recommendations, sslInfo);
        }
    }

    public static class SecurityHeaderDto {
        private Long id;
        private String headerName;
        private String headerValue;
        private boolean isPresent;
        private String securityRating;
        private String recommendation;

        public SecurityHeaderDto() {}

        public SecurityHeaderDto(Long id, String headerName, String headerValue, boolean isPresent, String securityRating, String recommendation) {
            this.id = id;
            this.headerName = headerName;
            this.headerValue = headerValue;
            this.isPresent = isPresent;
            this.securityRating = securityRating;
            this.recommendation = recommendation;
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

        public static SecurityHeaderDtoBuilder builder() {
            return new SecurityHeaderDtoBuilder();
        }

        public static class SecurityHeaderDtoBuilder {
            private Long id;
            private String headerName;
            private String headerValue;
            private boolean isPresent;
            private String securityRating;
            private String recommendation;

            public SecurityHeaderDtoBuilder id(Long id) { this.id = id; return this; }
            public SecurityHeaderDtoBuilder headerName(String headerName) { this.headerName = headerName; return this; }
            public SecurityHeaderDtoBuilder headerValue(String headerValue) { this.headerValue = headerValue; return this; }
            public SecurityHeaderDtoBuilder isPresent(boolean isPresent) { this.isPresent = isPresent; return this; }
            public SecurityHeaderDtoBuilder securityRating(String securityRating) { this.securityRating = securityRating; return this; }
            public SecurityHeaderDtoBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }

            public SecurityHeaderDto build() {
                return new SecurityHeaderDto(id, headerName, headerValue, isPresent, securityRating, recommendation);
            }
        }
    }

    public static class CookieAnalysisDto {
        private Long id;
        private String cookieName;
        private boolean isSecure;
        private boolean isHttpOnly;
        private String sameSite;
        private boolean isExpired;

        public CookieAnalysisDto() {}

        public CookieAnalysisDto(Long id, String cookieName, boolean isSecure, boolean isHttpOnly, String sameSite, boolean isExpired) {
            this.id = id;
            this.cookieName = cookieName;
            this.isSecure = isSecure;
            this.isHttpOnly = isHttpOnly;
            this.sameSite = sameSite;
            this.isExpired = isExpired;
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

        public static CookieAnalysisDtoBuilder builder() {
            return new CookieAnalysisDtoBuilder();
        }

        public static class CookieAnalysisDtoBuilder {
            private Long id;
            private String cookieName;
            private boolean isSecure;
            private boolean isHttpOnly;
            private String sameSite;
            private boolean isExpired;

            public CookieAnalysisDtoBuilder id(Long id) { this.id = id; return this; }
            public CookieAnalysisDtoBuilder cookieName(String cookieName) { this.cookieName = cookieName; return this; }
            public CookieAnalysisDtoBuilder isSecure(boolean isSecure) { this.isSecure = isSecure; return this; }
            public CookieAnalysisDtoBuilder isHttpOnly(boolean isHttpOnly) { this.isHttpOnly = isHttpOnly; return this; }
            public CookieAnalysisDtoBuilder sameSite(String sameSite) { this.sameSite = sameSite; return this; }
            public CookieAnalysisDtoBuilder isExpired(boolean isExpired) { this.isExpired = isExpired; return this; }

            public CookieAnalysisDto build() {
                return new CookieAnalysisDto(id, cookieName, isSecure, isHttpOnly, sameSite, isExpired);
            }
        }
    }

    public static class ScanRecommendationDto {
        private Long id;
        private String category;
        private String checkName;
        private String recommendation;

        public ScanRecommendationDto() {}

        public ScanRecommendationDto(Long id, String category, String checkName, String recommendation) {
            this.id = id;
            this.category = category;
            this.checkName = checkName;
            this.recommendation = recommendation;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getCheckName() { return checkName; }
        public void setCheckName(String checkName) { this.checkName = checkName; }

        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

        public static ScanRecommendationDtoBuilder builder() {
            return new ScanRecommendationDtoBuilder();
        }

        public static class ScanRecommendationDtoBuilder {
            private Long id;
            private String category;
            private String checkName;
            private String recommendation;

            public ScanRecommendationDtoBuilder id(Long id) { this.id = id; return this; }
            public ScanRecommendationDtoBuilder category(String category) { this.category = category; return this; }
            public ScanRecommendationDtoBuilder checkName(String checkName) { this.checkName = checkName; return this; }
            public ScanRecommendationDtoBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }

            public ScanRecommendationDto build() {
                return new ScanRecommendationDto(id, category, checkName, recommendation);
            }
        }
    }

    public static class SSLInfoDto {
        private Long id;
        private boolean isSslEnabled;
        private String protocol;
        private String cipherSuite;
        private String issuer;
        private LocalDateTime validFrom;
        private LocalDateTime validTo;
        private boolean isExpired;

        public SSLInfoDto() {}

        public SSLInfoDto(Long id, boolean isSslEnabled, String protocol, String cipherSuite, String issuer, 
                          LocalDateTime validFrom, LocalDateTime validTo, boolean isExpired) {
            this.id = id;
            this.isSslEnabled = isSslEnabled;
            this.protocol = protocol;
            this.cipherSuite = cipherSuite;
            this.issuer = issuer;
            this.validFrom = validFrom;
            this.validTo = validTo;
            this.isExpired = isExpired;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public boolean isSslEnabled() { return isSslEnabled; }
        public void setSslEnabled(boolean sslEnabled) { isSslEnabled = sslEnabled; }

        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }

        public String getCipherSuite() { return cipherSuite; }
        public void setCipherSuite(String cipherSuite) { this.cipherSuite = cipherSuite; }

        public String getIssuer() { return issuer; }
        public void setIssuer(String issuer) { this.issuer = issuer; }

        public LocalDateTime getValidFrom() { return validFrom; }
        public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

        public LocalDateTime getValidTo() { return validTo; }
        public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }

        public boolean isExpired() { return isExpired; }
        public void setExpired(boolean expired) { isExpired = expired; }

        public static SSLInfoDtoBuilder builder() {
            return new SSLInfoDtoBuilder();
        }

        public static class SSLInfoDtoBuilder {
            private Long id;
            private boolean isSslEnabled;
            private String protocol;
            private String cipherSuite;
            private String issuer;
            private LocalDateTime validFrom;
            private LocalDateTime validTo;
            private boolean isExpired;

            public SSLInfoDtoBuilder id(Long id) { this.id = id; return this; }
            public SSLInfoDtoBuilder isSslEnabled(boolean isSslEnabled) { this.isSslEnabled = isSslEnabled; return this; }
            public SSLInfoDtoBuilder protocol(String protocol) { this.protocol = protocol; return this; }
            public SSLInfoDtoBuilder cipherSuite(String cipherSuite) { this.cipherSuite = cipherSuite; return this; }
            public SSLInfoDtoBuilder issuer(String issuer) { this.issuer = issuer; return this; }
            public SSLInfoDtoBuilder validFrom(LocalDateTime validFrom) { this.validFrom = validFrom; return this; }
            public SSLInfoDtoBuilder validTo(LocalDateTime validTo) { this.validTo = validTo; return this; }
            public SSLInfoDtoBuilder isExpired(boolean isExpired) { this.isExpired = isExpired; return this; }

            public SSLInfoDto build() {
                return new SSLInfoDto(id, isSslEnabled, protocol, cipherSuite, issuer, validFrom, validTo, isExpired);
            }
        }
    }
}
