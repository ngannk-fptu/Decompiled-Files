/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 */
package com.atlassian.diagnostics.internal.jmx;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.internal.jmx.AlertsMXBean;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class Alerts
implements AlertsMXBean {
    private final AtomicLong error = new AtomicLong();
    private final AtomicLong info = new AtomicLong();
    private final AtomicLong warning = new AtomicLong();
    private volatile Instant latestTimestamp;

    @Override
    public long getErrorCount() {
        return this.error.get();
    }

    @Override
    public long getInfoCount() {
        return this.info.get();
    }

    @Override
    public Date getLatestAlertTimestamp() {
        Instant ts = this.latestTimestamp;
        return ts == null ? null : new Date(ts.toEpochMilli());
    }

    @Override
    public long getTotalCount() {
        return this.error.get() + this.info.get() + this.warning.get();
    }

    @Override
    public long getWarningCount() {
        return this.warning.get();
    }

    @Override
    public void reset() {
        this.error.set(0L);
        this.info.set(0L);
        this.warning.set(0L);
        this.latestTimestamp = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void onAlert(Alert alert) {
        switch (alert.getIssue().getSeverity()) {
            case INFO: {
                this.info.incrementAndGet();
                break;
            }
            case WARNING: {
                this.warning.incrementAndGet();
                break;
            }
            case ERROR: {
                this.error.incrementAndGet();
            }
        }
        Instant timestamp = alert.getTimestamp();
        Alerts alerts = this;
        synchronized (alerts) {
            if (this.latestTimestamp == null || this.latestTimestamp.isBefore(timestamp)) {
                this.latestTimestamp = timestamp;
            }
        }
    }
}

