/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.troubleshooting.stp.hercules.LogScanService;
import com.atlassian.troubleshooting.stp.scheduler.MonitoredJobRunner;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;

public class LogScanReportRunner
extends MonitoredJobRunner {
    private final LogScanService logScanService;

    public LogScanReportRunner(LogScanService logScanService) {
        this.logScanService = logScanService;
    }

    @Override
    protected TaskMonitor<Void> start() {
        return this.logScanService.sendLogScanReport();
    }
}

