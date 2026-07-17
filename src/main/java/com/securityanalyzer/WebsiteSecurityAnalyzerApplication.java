package com.securityanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WebsiteSecurityAnalyzerApplication {

    public static void main(String[] eloquenceArgs) {
        SpringApplication.run(WebsiteSecurityAnalyzerApplication.class, eloquenceArgs);
    }
}
