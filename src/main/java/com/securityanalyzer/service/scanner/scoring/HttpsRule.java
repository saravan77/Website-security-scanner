package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.ScanRecommendation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HttpsRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "HTTPS Support";
    }

    @Override
    public int getMaxPoints() {
        return 20;
    }

    @Override
    public RuleResult evaluate(ScanHistory scan) {
        String url = scan.getTargetUrl();
        if (url != null && url.toLowerCase().startsWith("https://")) {
            return RuleResult.success(20);
        }

        List<ScanRecommendation> recs = new ArrayList<>();
        recs.add(new ScanRecommendation(
                "HTTPS",
                "Insecure HTTP connection",
                "Your website is currently using unencrypted HTTP. Migrate to HTTPS by installing an SSL/TLS certificate and setting up redirect rules to force secure connections."
        ));
        return RuleResult.failure(0, recs);
    }
}
