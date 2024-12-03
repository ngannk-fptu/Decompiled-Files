/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.rest;

import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.rest.RestTaskStatus;

public class RestLogScanStatus
extends RestTaskStatus {
    public RestLogScanStatus(LogScanMonitor monitor) {
        super(monitor);
        this.put("logFilePath", monitor.getLogFilePath());
    }
}

