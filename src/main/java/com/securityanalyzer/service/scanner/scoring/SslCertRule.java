package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.entity.ScanRecommendation;
import com.securityanalyzer.entity.SSLInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SslCertRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "Valid SSL Certificate";
    }

    @Override
    public int getMaxPoints() {
        return 20;
    }

    @Override
    public RuleResult evaluate(ScanHistory scan) {
        SSLInfo ssl = scan.getSslInfo();
        List<ScanRecommendation> recs = new ArrayList<>();

        if (ssl == null || !ssl.isSslEnabled()) {
            recs.add(new ScanRecommendation(
                    "SSL",
                    "SSL certificate not found or disabled",
                    "An SSL/TLS certificate could not be retrieved. Install a valid SSL certificate (e.g., from Let's Encrypt) to enable secure encrypted transit."
            ));
            return RuleResult.failure(0, recs);
        }

        if (ssl.isExpired()) {
            recs.add(new ScanRecommendation(
                    "SSL",
                    "SSL certificate is expired",
                    "The SSL/TLS certificate on the server is expired. Renew your certificate immediately to avoid browser warnings and ensure security."
            ));
            return RuleResult.failure(0, recs);
        }

        return RuleResult.success(20);
    }
}
