package com.securityanalyzer.service.scanner;

import com.securityanalyzer.entity.CookieAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

@Service
public class CookieAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(CookieAnalyzer.class);

    public List<CookieAnalysis> analyze(List<String> setCookieHeaders) {
        List<CookieAnalysis> results = new ArrayList<>();
        if (setCookieHeaders == null || setCookieHeaders.isEmpty()) {
            return results;
        }

        for (String header : setCookieHeaders) {
            if (header == null || header.trim().isEmpty()) {
                continue;
            }
            try {
                List<HttpCookie> cookies = HttpCookie.parse(header);
                for (HttpCookie hc : cookies) {
                    String sameSite = "Not Specified";
                    String headerLower = header.toLowerCase();
                    if (headerLower.contains("samesite=strict")) {
                        sameSite = "Strict";
                    } else if (headerLower.contains("samesite=lax")) {
                        sameSite = "Lax";
                    } else if (headerLower.contains("samesite=none")) {
                        sameSite = "None";
                    }

                    CookieAnalysis analysis = CookieAnalysis.builder()
                            .cookieName(hc.getName())
                            .isSecure(hc.getSecure())
                            .isHttpOnly(hc.isHttpOnly())
                            .sameSite(sameSite)
                            .isExpired(hc.hasExpired())
                            .build();

                    results.add(analysis);
                }
            } catch (Exception e) {
                log.warn("Failed to parse cookie header: {}. Error: {}", header, e.getMessage());
            }
        }
        return results;
    }
}
