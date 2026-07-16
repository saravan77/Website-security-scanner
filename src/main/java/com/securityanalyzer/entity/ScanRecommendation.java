package com.securityanalyzer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "scan_recommendations")
public class ScanRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String checkName;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_history_id", nullable = false)
    private ScanHistory scanHistory;

    public ScanRecommendation() {}

    public ScanRecommendation(Long id, String category, String checkName, String recommendation, ScanHistory scanHistory) {
        this.id = id;
        this.category = category;
        this.checkName = checkName;
        this.recommendation = recommendation;
        this.scanHistory = scanHistory;
    }

    public ScanRecommendation(String category, String checkName, String recommendation) {
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

    public ScanHistory getScanHistory() { return scanHistory; }
    public void setScanHistory(ScanHistory scanHistory) { this.scanHistory = scanHistory; }

    public static ScanRecommendationBuilder builder() {
        return new ScanRecommendationBuilder();
    }

    public static class ScanRecommendationBuilder {
        private Long id;
        private String category;
        private String checkName;
        private String recommendation;
        private ScanHistory scanHistory;

        public ScanRecommendationBuilder id(Long id) { this.id = id; return this; }
        public ScanRecommendationBuilder category(String category) { this.category = category; return this; }
        public ScanRecommendationBuilder checkName(String checkName) { this.checkName = checkName; return this; }
        public ScanRecommendationBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public ScanRecommendationBuilder scanHistory(ScanHistory scanHistory) { this.scanHistory = scanHistory; return this; }

        public ScanRecommendation build() {
            return new ScanRecommendation(id, category, checkName, recommendation, scanHistory);
        }
    }
}
