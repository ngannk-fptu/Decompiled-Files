/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.cpu;

import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.OperatingSystemMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.OperatingSystemMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.cpu.CPUDiagnosticProvider;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.cpu.HighCPUUsageEvent;
import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import java.time.Duration;
import java.time.Instant;

public class CPUPerformancePoller
extends DiagnosticPoller<OperatingSystemMonitorConfiguration> {
    private final OperatingSystemMonitor operatingSystemMonitor;
    private final CPUDiagnosticProvider cpuDiagnosticProvider;
    private Duration consecutiveHighCpuUsageTime = Duration.ofMinutes(0L);

    public CPUPerformancePoller(OperatingSystemMonitorConfiguration config, OperatingSystemMonitor operatingSystemMonitor, CPUDiagnosticProvider cpuDiagnosticProvider) {
        super(CPUPerformancePoller.class.getName(), config);
        this.operatingSystemMonitor = operatingSystemMonitor;
        this.cpuDiagnosticProvider = cpuDiagnosticProvider;
    }

    @Override
    protected void execute() {
        double systemCpuLoad = this.cpuDiagnosticProvider.getDiagnostics().getSystemCpuLoad();
        this.updateConsecutiveHighCpuUsageTime(systemCpuLoad, ((OperatingSystemMonitorConfiguration)this.monitorConfiguration).getCpuUsagePercentageThreshold());
        if (this.consecutiveHighCpuUsageTime.toMinutes() >= ((OperatingSystemMonitorConfiguration)this.monitorConfiguration).getMaximumHighCpuUsageTime().toMinutes()) {
            this.operatingSystemMonitor.raiseAlertForHighCpu(Instant.now(), new HighCPUUsageEvent(systemCpuLoad));
        }
    }

    private void updateConsecutiveHighCpuUsageTime(double systemCpuLoad, double cpuUsageThreshold) {
        this.consecutiveHighCpuUsageTime = systemCpuLoad > cpuUsageThreshold ? Duration.ofMinutes(this.consecutiveHighCpuUsageTime.toMinutes() + 1L) : Duration.ofMinutes(0L);
    }
}

