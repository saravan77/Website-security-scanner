package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanRecommendation;
import java.util.ArrayList;
import java.util.List;

public class RuleResult {
    private final int pointsAchieved;
    private final List<ScanRecommendation> failedCheckRecommendations;

    public RuleResult(int pointsAchieved, List<ScanRecommendation> failedCheckRecommendations) {
        this.pointsAchieved = pointsAchieved;
        this.failedCheckRecommendations = failedCheckRecommendations != null ? failedCheckRecommendations : new ArrayList<>();
    }

    public int getPointsAchieved() {
        return pointsAchieved;
    }

    public List<ScanRecommendation> getFailedCheckRecommendations() {
        return failedCheckRecommendations;
    }

    public static RuleResult success(int points) {
        return new RuleResult(points, new ArrayList<>());
    }

    public static RuleResult failure(int points, List<ScanRecommendation> recommendations) {
        return new RuleResult(points, recommendations);
    }
}
