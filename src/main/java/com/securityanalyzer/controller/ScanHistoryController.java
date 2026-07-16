package com.securityanalyzer.controller;

import com.securityanalyzer.dto.CreateScanRequestDto;
import com.securityanalyzer.dto.ScanHistoryDto;
import com.securityanalyzer.entity.ScanHistory;
import com.securityanalyzer.repository.ScanHistoryRepository;
import com.securityanalyzer.service.PdfReportService;
import com.securityanalyzer.service.ScanHistoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scans")
public class ScanHistoryController {

    private final ScanHistoryService scanHistoryService;
    private final PdfReportService pdfReportService;
    private final ScanHistoryRepository scanHistoryRepository;

    @Autowired
    public ScanHistoryController(
            ScanHistoryService scanHistoryService,
            PdfReportService pdfReportService,
            ScanHistoryRepository scanHistoryRepository) {
        this.scanHistoryService = scanHistoryService;
        this.pdfReportService = pdfReportService;
        this.scanHistoryRepository = scanHistoryRepository;
    }

    @PostMapping
    public ResponseEntity<ScanHistoryDto> createScan(@Valid @RequestBody CreateScanRequestDto request) {
        ScanHistoryDto createdScan = scanHistoryService.executeScan(request);
        return new ResponseEntity<>(createdScan, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ScanHistoryDto>> getAllScans() {
        List<ScanHistoryDto> scans = scanHistoryService.getAllScans();
        return ResponseEntity.ok(scans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScanHistoryDto> getScanById(@PathVariable Long id) {
        ScanHistoryDto scan = scanHistoryService.getScanById(id);
        return ResponseEntity.ok(scan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScan(@PathVariable Long id) {
        scanHistoryService.deleteScan(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ScanHistoryDto> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam int score) {
        ScanHistoryDto updatedScan = scanHistoryService.updateScanStatus(id, status, score);
        return ResponseEntity.ok(updatedScan);
    }

    @GetMapping("/{id}/export/pdf")
    public void exportReportToPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
        ScanHistory scan = scanHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scan history record not found for ID: " + id));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=security-report-" + id + ".pdf");

        pdfReportService.generateScanReport(scan, response.getOutputStream());
    }
}
