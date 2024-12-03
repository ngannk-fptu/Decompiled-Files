/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.Issue
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.jmx;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.internal.jmx.IssueAlertsMXBean;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;

public class IssueAlerts
implements IssueAlertsMXBean {
    private final AtomicLong count;
    private final Issue issue;
    private volatile Instant latestTimestamp;

    public IssueAlerts(Issue issue) {
        this.issue = issue;
        this.count = new AtomicLong();
    }

    @Override
    @Nonnull
    public String getComponent() {
        return this.issue.getComponent().getName();
    }

    @Override
    public long getCount() {
        return this.count.get();
    }

    @Override
    @Nonnull
    public String getDescription() {
        return this.issue.getDescription();
    }

    @Override
    public Date getLatestAlertTimestamp() {
        Instant ts = this.latestTimestamp;
        return ts == null ? null : new Date(ts.toEpochMilli());
    }

    @Override
    @Nonnull
    public String getSeverity() {
        return this.issue.getSeverity().name();
    }

    @Override
    public void reset() {
        this.count.set(0L);
        this.latestTimestamp = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void onAlert(Alert alert) {
        this.count.incrementAndGet();
        Instant timestamp = alert.getTimestamp();
        IssueAlerts issueAlerts = this;
        synchronized (issueAlerts) {
            if (this.latestTimestamp == null || this.latestTimestamp.isBefore(timestamp)) {
                this.latestTimestamp = timestamp;
            }
        }
    }
}

