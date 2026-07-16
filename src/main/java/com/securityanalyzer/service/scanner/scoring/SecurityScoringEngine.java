package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.ScanRecommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityScoringEngine {

    private static final Logger log = LoggerFactory.getLogger(SecurityScoringEngine.class);

    private final List<ScoringRule> rules;

    @Autowired
    public SecurityScoringEngine(List<ScoringRule> rules) {
        this.rules = rules;
    }

    public int score(ScanHistory scan) {
        log.info("Starting security score calculation for target URL: {}", scan.getTargetUrl());
        int totalScore = 0;
        int totalMaxPoints = 0;

        for (ScoringRule rule : rules) {
            try {
                RuleResult result = rule.evaluate(scan);
                log.debug("Rule '{}' evaluated: points={}/{}", rule.getRuleName(), result.getPointsAchieved(), rule.getMaxPoints());
                totalScore += result.getPointsAchieved();
                totalMaxPoints += rule.getMaxPoints();

                // Attach generated recommendations to scan history
                for (ScanRecommendation rec : result.getFailedCheckRecommendations()) {
                    scan.addRecommendation(rec);
                }
            } catch (Exception e) {
                log.error("Error evaluating rule '{}': {}", rule.getRuleName(), e.getMessage(), e);
            }
        }

        log.info("Total security score computed: {}/{} for {}", totalScore, totalMaxPoints, scan.getTargetUrl());
        return totalScore;
    }
}
