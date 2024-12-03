/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.LineIterator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.confluence.healthcheck.jdk;

import com.atlassian.troubleshooting.api.healthcheck.LogFileHelper;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.File;
import java.io.Serializable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCHealthCheck
implements SupportHealthCheck {
    public static final int MAJOR_FAILURE_THRESHOLD = 5;
    public static final int CRITICAL_FAILURE_THRESHOLD = 30;
    private static final Logger LOGGER = LoggerFactory.getLogger(GCHealthCheck.class);
    private final LogFileHelper logFileHelper;
    private final SupportHealthStatusBuilder healthStatusBuilder;

    public GCHealthCheck(LogFileHelper logFileHelper, SupportHealthStatusBuilder healthStatusBuilder) {
        this.logFileHelper = logFileHelper;
        this.healthStatusBuilder = healthStatusBuilder;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SupportHealthStatus check() {
        double longestPauseSeconds = 0.0;
        File logDir = this.logFileHelper.getGCLogDir();
        if (logDir == null) {
            return this.healthStatusBuilder.warning(this, "confluence.healthcheck.gc.log.dir.missing.fail", new Serializable[0]);
        }
        File currentLog = this.logFileHelper.getCurrentGCLog(logDir);
        if (currentLog == null) {
            return this.healthStatusBuilder.warning(this, "confluence.healthcheck.gc.log.missing.fail", new Serializable[]{this.logFileHelper.getGCLogDir().getAbsolutePath()});
        }
        LineIterator lineIterator = null;
        try {
            lineIterator = FileUtils.lineIterator((File)currentLog, (String)"UTF-8");
            while (lineIterator.hasNext()) {
                double pauseTime;
                String line = lineIterator.nextLine();
                if ((line.contains("GC pause") || line.contains("GC cleanup")) && line.endsWith(" secs]")) {
                    String pauseTimeLine = line.substring(0, line.lastIndexOf(" "));
                    double pauseTime2 = Double.parseDouble(pauseTimeLine = pauseTimeLine.substring(pauseTimeLine.lastIndexOf(" ")));
                    if (!(pauseTime2 > longestPauseSeconds)) continue;
                    longestPauseSeconds = pauseTime2;
                    continue;
                }
                if (!line.startsWith(", ") || !((pauseTime = Double.parseDouble(line.substring(2, line.indexOf(" secs]")))) > longestPauseSeconds)) continue;
                longestPauseSeconds = pauseTime;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), (Throwable)e);
            SupportHealthStatus supportHealthStatus = this.healthStatusBuilder.warning(this, "confluence.healthcheck.gc.log.processing.fail", new Serializable[]{currentLog.getAbsolutePath()});
            return supportHealthStatus;
        }
        finally {
            LineIterator.closeQuietly((LineIterator)lineIterator);
        }
        if (longestPauseSeconds > 30.0) {
            return this.healthStatusBuilder.critical(this, "confluence.healthcheck.gc.pausetime.30.fail", Double.valueOf(longestPauseSeconds));
        }
        if (longestPauseSeconds > 5.0) {
            return this.healthStatusBuilder.major(this, "confluence.healthcheck.gc.pausetime.5.fail", Double.valueOf(longestPauseSeconds));
        }
        return this.healthStatusBuilder.ok(this, "confluence.healthcheck.gc.pausetime.valid", Double.valueOf(longestPauseSeconds));
    }
}

