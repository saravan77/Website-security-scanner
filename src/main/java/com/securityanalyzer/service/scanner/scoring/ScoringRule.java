package com.securityanalyzer.service.scanner.scoring;

import com.securityanalyzer.entity.ScanHistory;

public interface ScoringRule {
    String getRuleName();
    int getMaxPoints();
    RuleResult evaluate(ScanHistory scan);
}
