package com.securityanalyzer.service.scanner;

import com.securityanalyzer.entity.SecurityHeader;
import org.springframework.stereotype.Service;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityHeaderAnalyzer {

    public List<SecurityHeader> analyze(HttpHeaders headers) {
        List<SecurityHeader> results = new ArrayList<>();
        if (headers == null) {
            // If headers are null, all headers are missing
            results.add(createHeaderResult("Content-Security-Policy", null));
            results.add(createHeaderResult("Strict-Transport-Security", null));
            results.add(createHeaderResult("X-Frame-Options", null));
            results.add(createHeaderResult("X-Content-Type-Options", null));
            results.add(createHeaderResult("Referrer-Policy", null));
            return results;
        }

        results.add(createHeaderResult("Content-Security-Policy", headers.firstValue("Content-Security-Policy").orElse(null)));
        results.add(createHeaderResult("Strict-Transport-Security", headers.firstValue("Strict-Transport-Security").orElse(null)));
        results.add(createHeaderResult("X-Frame-Options", headers.firstValue("X-Frame-Options").orElse(null)));
        results.add(createHeaderResult("X-Content-Type-Options", headers.firstValue("X-Content-Type-Options").orElse(null)));
        results.add(createHeaderResult("Referrer-Policy", headers.firstValue("Referrer-Policy").orElse(null)));

        return results;
    }

    private SecurityHeader createHeaderResult(String name, String value) {
        boolean isPresent = value != null && !value.trim().isEmpty();
        String rating;
        String recommendation;

        switch (name) {
            case "Content-Security-Policy":
                if (isPresent) {
                    rating = "SECURE";
                    recommendation = "Content-Security-Policy is present. This helps mitigate Cross-Site Scripting (XSS) and other injection attacks.";
                } else {
                    rating = "DANGER";
                    recommendation = "Content-Security-Policy (CSP) is missing! Implement CSP to restrict the sources of content that the browser is allowed to load.";
                }
                break;
            case "Strict-Transport-Security":
                if (isPresent) {
                    rating = "SECURE";
                    recommendation = "Strict-Transport-Security (HSTS) is present. This forces the browser to communicate over secure HTTPS connections only.";
                } else {
                    rating = "WARNING";
                    recommendation = "Strict-Transport-Security (HSTS) is missing! Implement HSTS to protect against protocol downgrade and cookie hijacking attacks.";
                }
                break;
            case "X-Frame-Options":
                if (isPresent) {
                    rating = "SECURE";
                    recommendation = "X-Frame-Options is present. This protects your users against Clickjacking attacks.";
                } else {
                    rating = "WARNING";
                    recommendation = "X-Frame-Options is missing! Implement X-Frame-Options (DENY or SAMEORIGIN) to prevent your site from being embedded in an iframe on other websites.";
                }
                break;
            case "X-Content-Type-Options":
                if (isPresent && "nosniff".equalsIgnoreCase(value.trim())) {
                    rating = "SECURE";
                    recommendation = "X-Content-Type-Options is present and set to 'nosniff'. This prevents the browser from MIME-sniffing response types.";
                } else if (isPresent) {
                    rating = "WARNING";
                    recommendation = "X-Content-Type-Options is present but not set to 'nosniff'. Configure it to 'nosniff' to disable MIME-type sniffing.";
                } else {
                    rating = "WARNING";
                    recommendation = "X-Content-Type-Options is missing! Set this header to 'nosniff' to ensure browsers respect the declared content types.";
                }
                break;
            case "Referrer-Policy":
                if (isPresent) {
                    rating = "SECURE";
                    recommendation = "Referrer-Policy is present. This controls how much referrer information is sent with outbound links.";
                } else {
                    rating = "WARNING";
                    recommendation = "Referrer-Policy is missing! Implement Referrer-Policy (e.g., 'no-referrer-when-downgrade' or 'strict-origin-when-cross-origin') to protect sensitive user details.";
                }
                break;
            default:
                rating = "NONE";
                recommendation = "";
        }

        return SecurityHeader.builder()
                .headerName(name)
                .headerValue(value)
                .isPresent(isPresent)
                .securityRating(rating)
                .recommendation(recommendation)
                .build();
    }
}
