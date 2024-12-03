/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.troubleshooting.stp.hercules.LogScanResult;
import com.atlassian.troubleshooting.stp.task.DefaultTaskMonitor;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang.StringUtils;

@ParametersAreNonnullByDefault
public class LogScanMonitor
extends DefaultTaskMonitor<LogScanResult> {
    private static final long serialVersionUID = 2L;
    private static final String LOG_FILE_PATH = "logFilePath";
    private static final String CANCELLED_OR_DONE = "cancelledOrDone";
    private String logFilePath;

    public LogScanMonitor() {
    }

    public LogScanMonitor(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Nonnull
    public String getLogFilePath() {
        return this.logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.setCustomAttributes(Collections.singletonMap(LOG_FILE_PATH, logFilePath));
    }

    @Override
    protected void addCustomAttributes(Map<String, Serializable> attributesToUpdate) {
        Objects.requireNonNull(attributesToUpdate);
        if (StringUtils.isNotBlank((String)this.logFilePath)) {
            attributesToUpdate.put(LOG_FILE_PATH, (Serializable)((Object)this.logFilePath));
        }
        attributesToUpdate.put(CANCELLED_OR_DONE, Boolean.valueOf(this.isCancelled() || this.isDone()));
    }

    @Override
    public void setCustomAttributes(Map<String, Serializable> attributesToRead) {
        Objects.requireNonNull(attributesToRead);
        this.logFilePath = (String)((Object)attributesToRead.get(LOG_FILE_PATH));
        Serializable cancelledOrDone = attributesToRead.getOrDefault(CANCELLED_OR_DONE, Boolean.valueOf(false));
        if (Boolean.parseBoolean(String.valueOf(cancelledOrDone))) {
            this.cancel(true);
        }
    }
}

