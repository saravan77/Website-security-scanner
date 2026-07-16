package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.ScanRecommendation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedirectSafetyRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "Redirect Safety";
    }

    @Override
    public int getMaxPoints() {
        return 10;
    }

    @Override
    public RuleResult evaluate(ScanHistory scan) {
        int redirects = scan.getRedirectCount();
        List<ScanRecommendation> recs = new ArrayList<>();

        if (redirects == 0) {
            return RuleResult.success(10);
        }

        if (redirects >= 4) {
            recs.add(new ScanRecommendation(
                    "REDIRECTS",
                    "Excessive redirects detected",
                    "The website performed " + redirects + " redirects. Excessive redirects increase page loading latency and could indicate circular routing or open redirect vulnerabilities."
            ));
            return RuleResult.failure(5, recs);
        }

        // Safe redirect trace
        return RuleResult.success(10);
    }
}
