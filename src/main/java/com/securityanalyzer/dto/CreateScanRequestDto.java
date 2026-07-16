package com.securityanalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateScanRequestDto {

    @NotBlank(message = "Target URL cannot be blank")
    @Pattern(
        regexp = "^https?://[-a-zA-Z0-9+&@#/%?\\(\\)=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$",
        message = "Must be a valid HTTP or HTTPS target URL (e.g., https://example.com)"
    )
    private String targetUrl;

    public CreateScanRequestDto() {}

    public CreateScanRequestDto(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public static CreateScanRequestDtoBuilder builder() {
        return new CreateScanRequestDtoBuilder();
    }

    public static class CreateScanRequestDtoBuilder {
        private String targetUrl;

        public CreateScanRequestDtoBuilder targetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
            return this;
        }

        public CreateScanRequestDto build() {
            return new CreateScanRequestDto(targetUrl);
        }
    }
}
