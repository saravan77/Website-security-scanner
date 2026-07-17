package com.securityanalyzer.service;

import com.securityanalyzer.entity.ScanHistory;
import java.io.OutputStream;

public interface PdfReportService {
    void generateScanReport(ScanHistory scan, OutputStream os);
    void generateScanReportCustom(ScanHistory scan, OutputStream os, boolean excludeRecommendations, boolean excludeSsl, boolean excludeHeaders, boolean excludeCookies);
}
