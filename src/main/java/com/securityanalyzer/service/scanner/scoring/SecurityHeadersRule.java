package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.ScanRecommendation;
import com.securityanalyzer.entity.SecurityHeader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SecurityHeadersRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "Security Headers";
    }

    @Override
    public int getMaxPoints() {
        return 30;
    }

    @Override
    public RuleResult evaluate(ScanHistory scan) {
        List<SecurityHeader> headers = scan.getSecurityHeaders();
        List<ScanRecommendation> recs = new ArrayList<>();
        int achievedPoints = 0;

        if (headers == null || headers.isEmpty()) {
            addMissingHeaderRec("Content-Security-Policy", recs);
            addMissingHeaderRec("Strict-Transport-Security", recs);
            addMissingHeaderRec("X-Frame-Options", recs);
            addMissingHeaderRec("X-Content-Type-Options", recs);
            addMissingHeaderRec("Referrer-Policy", recs);
            return RuleResult.failure(0, recs);
        }

        for (String name : List.of("Content-Security-Policy", "Strict-Transport-Security", "X-Frame-Options", "X-Content-Type-Options", "Referrer-Policy")) {
            SecurityHeader match = headers.stream()
                    .filter(h -> name.equalsIgnoreCase(h.getHeaderName()))
                    .findFirst()
                    .orElse(null);

            if (match != null && match.isPresent() && "SECURE".equalsIgnoreCase(match.getSecurityRating())) {
                achievedPoints += 6;
            } else {
                addMissingHeaderRec(name, recs);
            }
        }

        return RuleResult.failure(achievedPoints, recs);
    }

    private void addMissingHeaderRec(String name, List<ScanRecommendation> recs) {
        String recText;
        switch (name) {
            case "Content-Security-Policy":
                recText = "Content-Security-Policy (CSP) is missing or misconfigured. Add a CSP header to prevent Cross-Site Scripting (XSS), data injection, and clickjacking attacks.";
                break;
            case "Strict-Transport-Security":
                recText = "Strict-Transport-Security (HSTS) is missing. Set the 'Strict-Transport-Security' header on your server to enforce HTTPS-only connections.";
                break;
            case "X-Frame-Options":
                recText = "X-Frame-Options is missing. Configure 'X-Frame-Options: DENY' or 'SAMEORIGIN' to protect your site against Clickjacking attacks.";
                break;
            case "X-Content-Type-Options":
                recText = "X-Content-Type-Options is missing or not set to 'nosniff'. Set 'X-Content-Type-Options: nosniff' to disable MIME-type sniffing.";
                break;
            case "Referrer-Policy":
                recText = "Referrer-Policy is missing. Add a Referrer-Policy header (e.g., 'strict-origin-when-cross-origin') to control how much referrer details are shared.";
                break;
            default:
                recText = "Configure security header " + name + " properly.";
        }
        recs.add(new ScanRecommendation("HEADERS", name + " Check Failed", recText));
    }
}
