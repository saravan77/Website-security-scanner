package com.securityanalyzer.service;

import com.securityanalyzer.dto.CreateScanRequestDto;
import com.securityanalyzer.dto.ScanHistoryDto;

import java.util.List;

public interface ScanHistoryService {
    ScanHistoryDto createScanPlaceholder(CreateScanRequestDto request);
    ScanHistoryDto executeScan(CreateScanRequestDto request);
    List<ScanHistoryDto> getAllScans();
    ScanHistoryDto getScanById(Long id);
    void deleteScan(Long id);
    ScanHistoryDto updateScanStatus(Long id, String status, int score);
}
