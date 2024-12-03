/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.troubleshooting.stp.hercules.LogScanHelper;
import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.hercules.LogScanReportSettings;
import com.atlassian.troubleshooting.stp.hercules.LogScanReportTask;
import com.atlassian.troubleshooting.stp.hercules.LogScanTask;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.task.DefaultTaskMonitor;
import java.io.File;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class LogScanFactory {
    private final SupportApplicationInfo applicationInfo;
    private final LogScanHelper logScanHelper;
    private final MailUtility mailUtility;

    @Autowired
    public LogScanFactory(SupportApplicationInfo applicationInfo, LogScanHelper logScanHelper, MailUtility mailUtility) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.logScanHelper = Objects.requireNonNull(logScanHelper);
        this.mailUtility = Objects.requireNonNull(mailUtility);
    }

    @Nonnull
    LogScanTask createLogScanTask(File logFile) {
        Objects.requireNonNull(logFile);
        return new LogScanTask(logFile, this.applicationInfo, this.logScanHelper, new LogScanMonitor(logFile.getPath()));
    }

    @Nonnull
    LogScanReportTask createLogScanReportTask(LogScanReportSettings reportSettings) {
        return new LogScanReportTask(reportSettings, this.createLogScanTask(this.applicationInfo.getPrimaryApplicationLog()), this.applicationInfo, this.mailUtility, this.logScanHelper, new DefaultTaskMonitor<Void>());
    }
}

