package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.ScanRecommendation;
import com.securityanalyzer.entity.CookieAnalysis;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CookiesRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "Cookies Security";
    }

    @Override
    public int getMaxPoints() {
        return 15;
    }

    @Override
    public RuleResult evaluate(ScanHistory scan) {
        List<CookieAnalysis> cookies = scan.getCookieAnalyses();
        List<ScanRecommendation> recs = new ArrayList<>();

        if (cookies == null || cookies.isEmpty()) {
            return RuleResult.success(15);
        }

        int deductions = 0;
        for (CookieAnalysis cookie : cookies) {
            boolean httpOnly = cookie.isHttpOnly();
            boolean secure = cookie.isSecure();

            if (!httpOnly || !secure) {
                StringBuilder recText = new StringBuilder();
                recText.append("Cookie '").append(cookie.getCookieName()).append("' is insecure. ");
                List<String> issues = new ArrayList<>();
                if (!httpOnly) {
                    issues.add("missing 'HttpOnly' flag (exposes cookie to client script access)");
                    deductions += 3;
                }
                if (!secure) {
                    issues.add("missing 'Secure' flag (exposes cookie to intercept during cleartext transit)");
                    deductions += 3;
                }
                recText.append("It is ").append(String.join(" and ", issues)).append(". Configure it on the server to mitigate session hijacking.");
                
                recs.add(new ScanRecommendation(
                        "COOKIES",
                        "Insecure Cookie: " + cookie.getCookieName(),
                        recText.toString()
                ));
            }
        }

        int score = Math.max(0, 15 - deductions);
        return RuleResult.failure(score, recs);
    }
}
