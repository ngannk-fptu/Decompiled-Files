/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.OperatingSystemMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.cpu.HighCPUUsageEvent;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.text.DecimalFormat;
import java.time.Instant;
import javax.annotation.Nonnull;

public class OperatingSystemMonitor
extends InitializingMonitor {
    private static final int CPU_HIGH_USAGE_ISSUE_ID = 2001;
    private static final int SYSTEM_LOAD_AVERAGE_INCREASED_ISSUE_ID = 2002;
    private static final int LOW_RAM_MEMORY_ID = 2003;
    private static final int LOW_FREE_DIRECTORY_DISK_SPACE_ID = 2004;
    private static final int INACCESSIBLE_SYSTEM_DIRECTORY_ID = 2005;
    private static final String KEY_PREFIX = "diagnostics.operating.system.issue";
    private static final String CPU_USAGE_KEY = "cpuUsagePercentage";
    private final OperatingSystemMonitorConfiguration operatingSystemMonitorConfiguration;

    public OperatingSystemMonitor(OperatingSystemMonitorConfiguration operatingSystemMonitorConfiguration) {
        this.operatingSystemMonitorConfiguration = operatingSystemMonitorConfiguration;
    }

    public void init(MonitoringService monitoringService) {
        this.monitor = monitoringService.createMonitor("CPU", "diagnostics.operating.system.name", (MonitorConfiguration)this.operatingSystemMonitorConfiguration);
        this.defineIssue(KEY_PREFIX, 2001, Severity.WARNING);
        this.defineIssue(KEY_PREFIX, 2002, Severity.WARNING);
        this.defineIssue(KEY_PREFIX, 2003, Severity.WARNING, null);
        this.defineIssue(KEY_PREFIX, 2004, Severity.WARNING, null);
        this.defineIssue(KEY_PREFIX, 2005, Severity.WARNING, null);
    }

    public void raiseAlertForHighCpu(@Nonnull Instant timestamp, @Nonnull HighCPUUsageEvent highCpuUsageEvent) {
        this.alert(2001, builder -> builder.timestamp(timestamp).details(() -> ImmutableMap.of((Object)CPU_USAGE_KEY, (Object)new DecimalFormat("#.##").format(highCpuUsageEvent.getPercentage()))));
    }

    public void alertLowFreeMemory(long free, long total, long minimum) {
        this.alert(2003, builder -> builder.timestamp(Instant.now()).details(() -> this.createBaseAlert(free, total, minimum).build()).build());
    }

    public void alertLowFreeDiskSpace(File directory, long free, long total, long minimum) {
        this.alert(2004, builder -> builder.timestamp(Instant.now()).details(() -> this.createBaseAlert(free, total, minimum).put((Object)"directory", (Object)directory.getAbsolutePath()).build()));
    }

    public void alertFileSystemInaccessible(File file) {
        this.alert(2005, builder -> builder.timestamp(Instant.now()).details(() -> ImmutableMap.of((Object)"directory", (Object)file.getAbsolutePath())).build());
    }

    private ImmutableMap.Builder<Object, Object> createBaseAlert(long freeMemory, long totalMemory, long minimumMemory) {
        return ImmutableMap.builder().put((Object)"freeMemoryInMegabytes", (Object)freeMemory).put((Object)"totalMemoryInMegaBytes", (Object)totalMemory).put((Object)"minimumMemoryInMegaBytes", (Object)minimumMemory);
    }
}

