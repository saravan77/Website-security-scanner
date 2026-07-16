package com.securityanalyzer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ssl_infos")
public class SSLInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isSslEnabled;

    private String protocol; // E.g., TLSv1.3, TLSv1.2

    private String cipherSuite;

    private String issuer;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private boolean isExpired;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_history_id", nullable = false)
    private ScanHistory scanHistory;

    public SSLInfo() {}

    public SSLInfo(Long id, boolean isSslEnabled, String protocol, String cipherSuite, String issuer, 
                   LocalDateTime validFrom, LocalDateTime validTo, boolean isExpired, ScanHistory scanHistory) {
        this.id = id;
        this.isSslEnabled = isSslEnabled;
        this.protocol = protocol;
        this.cipherSuite = cipherSuite;
        this.issuer = issuer;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isExpired = isExpired;
        this.scanHistory = scanHistory;
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

    public ScanHistory getScanHistory() { return scanHistory; }
    public void setScanHistory(ScanHistory scanHistory) { this.scanHistory = scanHistory; }

    public static SSLInfoBuilder builder() {
        return new SSLInfoBuilder();
    }

    public static class SSLInfoBuilder {
        private Long id;
        private boolean isSslEnabled;
        private String protocol;
        private String cipherSuite;
        private String issuer;
        private LocalDateTime validFrom;
        private LocalDateTime validTo;
        private boolean isExpired;
        private ScanHistory scanHistory;

        public SSLInfoBuilder id(Long id) { this.id = id; return this; }
        public SSLInfoBuilder isSslEnabled(boolean isSslEnabled) { this.isSslEnabled = isSslEnabled; return this; }
        public SSLInfoBuilder protocol(String protocol) { this.protocol = protocol; return this; }
        public SSLInfoBuilder cipherSuite(String cipherSuite) { this.cipherSuite = cipherSuite; return this; }
        public SSLInfoBuilder issuer(String issuer) { this.issuer = issuer; return this; }
        public SSLInfoBuilder validFrom(LocalDateTime validFrom) { this.validFrom = validFrom; return this; }
        public SSLInfoBuilder validTo(LocalDateTime validTo) { this.validTo = validTo; return this; }
        public SSLInfoBuilder isExpired(boolean isExpired) { this.isExpired = isExpired; return this; }
        public SSLInfoBuilder scanHistory(ScanHistory scanHistory) { this.scanHistory = scanHistory; return this; }

        public SSLInfo build() {
            return new SSLInfo(id, isSslEnabled, protocol, cipherSuite, issuer, validFrom, validTo, isExpired, scanHistory);
        }
    }
}
