package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.ScanRecommendation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseTimeRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "Response Time";
    }

    @Override
    public int getMaxPoints() {
        return 5;
    }

    @Override
    public RuleResult evaluate(ScanHistory scan) {
        Long responseTime = scan.getResponseTimeMs();
        List<ScanRecommendation> recs = new ArrayList<>();

        if (responseTime == null) {
            recs.add(new ScanRecommendation(
                    "PERFORMANCE",
                    "No response time measured",
                    "The website was unreachable or did not respond. Verify server availability and network connectivity."
            ));
            return RuleResult.failure(0, recs);
        }

        if (responseTime < 500) {
            return RuleResult.success(5);
        } else if (responseTime < 1000) {
            return RuleResult.success(3);
        } else if (responseTime < 2000) {
            recs.add(new ScanRecommendation(
                    "PERFORMANCE",
                    "Slow server response time",
                    "The website response time was " + responseTime + " ms. Slow response times affect user experience. Optimize server configurations, database queries, or implement local caching."
            ));
            return RuleResult.failure(1, recs);
        } else {
            recs.add(new ScanRecommendation(
                    "PERFORMANCE",
                    "Critically slow server response time",
                    "The website response time was " + responseTime + " ms. This is extremely slow. Implement a Content Delivery Network (CDN), optimize media assets, or scale server hardware."
            ));
            return RuleResult.failure(0, recs);
        }
    }
}
