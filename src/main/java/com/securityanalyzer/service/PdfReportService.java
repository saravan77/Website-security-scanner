package com.securityanalyzer.service;

import com.securityanalyzer.entity.ScanHistory;
import java.io.OutputStream;

public interface PdfReportService {
    void generateScanReport(ScanHistory scan, OutputStream os);
}
