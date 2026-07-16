package com.securityanalyzer.dto;

public class ScanRequestDto {
    private String targetUrl;

    public ScanRequestDto() {}

    public ScanRequestDto(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public static ScanRequestDtoBuilder builder() {
        return new ScanRequestDtoBuilder();
    }

    public static class ScanRequestDtoBuilder {
        private String targetUrl;

        public ScanRequestDtoBuilder targetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
            return this;
        }

        public ScanRequestDto build() {
            return new ScanRequestDto(targetUrl);
        }
    }
}
