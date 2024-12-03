/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.hercules.LogScanReportSettings;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface LogScanService {
    @Nonnull
    public LogScanReportSettings getReportSettings();

    public void setReportSettings(LogScanReportSettings var1);

    @Nullable
    public LogScanMonitor getMonitor(String var1);

    public void cancelScan(String var1);

    @Nullable
    public LogScanMonitor getLatestScan();

    @Nonnull
    public TaskMonitor<Void> sendLogScanReport();

    @Nonnull
    public LogScanMonitor scan(File var1);

    @Nullable
    public LogScanMonitor getLastScan();

    public void clearScanResultCache();
}

