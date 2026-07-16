package com.securityanalyzer.service.scanner;

import com.securityanalyzer.config.ScanConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class HttpScanner {

    private static final Logger log = LoggerFactory.getLogger(HttpScanner.class);

    private final ScanConfig scanConfig;

    @Autowired
    public HttpScanner(ScanConfig scanConfig) {
        this.scanConfig = scanConfig;
    }

    public static class HttpScanResult {
        private boolean isAvailable;
        private int statusCode;
        private long responseTimeMs;
        private int redirectCount;
        private String finalUrl;
        private HttpHeaders headers;
        private List<String> setCookieHeaders;
        private String exceptionMessage;

        public HttpScanResult() {}

        public HttpScanResult(boolean isAvailable, int statusCode, long responseTimeMs, int redirectCount, 
                              String finalUrl, HttpHeaders headers, List<String> setCookieHeaders, String exceptionMessage) {
            this.isAvailable = isAvailable;
            this.statusCode = statusCode;
            this.responseTimeMs = responseTimeMs;
            this.redirectCount = redirectCount;
            this.finalUrl = finalUrl;
            this.headers = headers;
            this.setCookieHeaders = setCookieHeaders;
            this.exceptionMessage = exceptionMessage;
        }

        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { isAvailable = available; }

        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

        public long getResponseTimeMs() { return responseTimeMs; }
        public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

        public int getRedirectCount() { return redirectCount; }
        public void setRedirectCount(int redirectCount) { this.redirectCount = redirectCount; }

        public String getFinalUrl() { return finalUrl; }
        public void setFinalUrl(String finalUrl) { this.finalUrl = finalUrl; }

        public HttpHeaders getHeaders() { return headers; }
        public void setHeaders(HttpHeaders headers) { this.headers = headers; }

        public List<String> getSetCookieHeaders() { return setCookieHeaders; }
        public void setSetCookieHeaders(List<String> setCookieHeaders) { this.setCookieHeaders = setCookieHeaders; }

        public String getExceptionMessage() { return exceptionMessage; }
        public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }

        public static HttpScanResultBuilder builder() {
            return new HttpScanResultBuilder();
        }

        public static class HttpScanResultBuilder {
            private boolean isAvailable;
            private int statusCode;
            private long responseTimeMs;
            private int redirectCount;
            private String finalUrl;
            private HttpHeaders headers;
            private List<String> setCookieHeaders = new ArrayList<>();
            private String exceptionMessage;

            public HttpScanResultBuilder isAvailable(boolean isAvailable) { this.isAvailable = isAvailable; return this; }
            public HttpScanResultBuilder statusCode(int statusCode) { this.statusCode = statusCode; return this; }
            public HttpScanResultBuilder responseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; return this; }
            public HttpScanResultBuilder redirectCount(int redirectCount) { this.redirectCount = redirectCount; return this; }
            public HttpScanResultBuilder finalUrl(String finalUrl) { this.finalUrl = finalUrl; return this; }
            public HttpScanResultBuilder headers(HttpHeaders headers) { this.headers = headers; return this; }
            public HttpScanResultBuilder setCookieHeaders(List<String> setCookieHeaders) { this.setCookieHeaders = setCookieHeaders; return this; }
            public HttpScanResultBuilder exceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; return this; }

            public HttpScanResult build() {
                return new HttpScanResult(isAvailable, statusCode, responseTimeMs, redirectCount, finalUrl, headers, setCookieHeaders, exceptionMessage);
            }
        }
    }

    public HttpScanResult scan(String targetUrl, int timeoutMs) {
        log.info("Executing HTTP scan for target URL: {}", targetUrl);
        String currentUrl = targetUrl;
        int redirectCount = 0;
        long totalResponseTime = 0;
        List<String> accumulatedCookies = new ArrayList<>();
        HttpResponse<Void> response = null;

        try {
            int finalTimeout = timeoutMs > 0 ? timeoutMs : scanConfig.getConnectTimeoutMs();
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(finalTimeout))
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();

            while (true) {
                log.debug("HTTP request to: {}", currentUrl);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(currentUrl))
                        .timeout(Duration.ofMillis(finalTimeout))
                        .header("User-Agent", "Website-Security-Analyzer/1.0 (Security Scanner)")
                        .header("Accept", "*/*")
                        .GET()
                        .build();

                long startTime = System.currentTimeMillis();
                response = client.send(request, HttpResponse.BodyHandlers.discarding());
                long duration = System.currentTimeMillis() - startTime;
                totalResponseTime += duration;

                List<String> cookies = response.headers().allValues("set-cookie");
                if (cookies != null) {
                    accumulatedCookies.addAll(cookies);
                }

                int statusCode = response.statusCode();
                log.debug("HTTP response code: {} for {}", statusCode, currentUrl);

                if (statusCode >= 300 && statusCode < 400) {
                    String location = response.headers().firstValue("location").orElse(null);
                    if (location == null || location.isEmpty()) {
                        log.debug("Redirect response code {} but Location header is missing or empty.", statusCode);
                        break;
                    }

                    URI currentUri = URI.create(currentUrl);
                    URI resolvedUri = currentUri.resolve(location);
                    currentUrl = resolvedUri.toString();
                    redirectCount++;

                    if (redirectCount >= scanConfig.getMaxRedirects()) {
                        log.warn("Exceeded max redirect count ({}). Stopping redirect tracing.", scanConfig.getMaxRedirects());
                        break;
                    }
                } else {
                    break;
                }
            }

            return HttpScanResult.builder()
                    .isAvailable(true)
                    .statusCode(response != null ? response.statusCode() : 0)
                    .responseTimeMs(totalResponseTime)
                    .redirectCount(redirectCount)
                    .finalUrl(currentUrl)
                    .headers(response != null ? response.headers() : null)
                    .setCookieHeaders(accumulatedCookies)
                    .build();

        } catch (Exception e) {
            log.error("HTTP scan encountered an error for URL: {}. Error: {}", targetUrl, e.getMessage(), e);
            return HttpScanResult.builder()
                    .isAvailable(false)
                    .statusCode(0)
                    .responseTimeMs(totalResponseTime)
                    .redirectCount(redirectCount)
                    .finalUrl(currentUrl)
                    .setCookieHeaders(accumulatedCookies)
                    .exceptionMessage(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())
                    .build();
        }
    }
}
