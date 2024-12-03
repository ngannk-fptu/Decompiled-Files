/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.ScheduleInterval;
import java.time.Duration;
import javax.annotation.Nonnull;

public interface OperatingSystemMonitorConfiguration
extends MonitorConfiguration {
    public double getCpuUsagePercentageThreshold();

    @Nonnull
    public Duration getMaximumHighCpuUsageTime();

    public long directoryMinimumMegabytesOfFreeDiskSpace();

    public long minimumMegabytesOfRam();

    public ScheduleInterval cpuPerformancePollerScheduleInterval();

    public ScheduleInterval fileDirectoryPollerScheduleInterval();

    public ScheduleInterval ramPollerScheduleInterval();
}

