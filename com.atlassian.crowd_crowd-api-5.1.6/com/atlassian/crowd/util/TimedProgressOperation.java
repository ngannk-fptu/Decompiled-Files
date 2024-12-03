/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.util.Percentage;
import com.atlassian.crowd.util.TimedOperation;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

public class TimedProgressOperation
extends TimedOperation {
    private final int total;
    private int progress = 0;
    private final String action;
    private final Logger log;
    private final long gapTime;
    long latestLog = this.start;

    public TimedProgressOperation(String action, int total, Logger log) {
        this(action, total, log, TimeUnit.MINUTES.toMillis(1L));
    }

    public TimedProgressOperation(String action, int total, Logger log, long gapTime) {
        this.action = action;
        this.total = total;
        this.log = log;
        this.gapTime = gapTime;
    }

    public void incrementProgress() {
        this.logIfNecessary(false);
        ++this.progress;
    }

    public void incrementedProgress() {
        ++this.progress;
        this.logIfNecessary(false);
    }

    private String getFormatMessage(long now) {
        return this.action + String.format(" - (%d/%d - %s%%) %dms elapsed", this.progress, this.total, Percentage.get(this.progress, this.total).toString(), now - this.start);
    }

    private void logIfNecessary(boolean finished) {
        boolean logAtTrace;
        if (!this.log.isInfoEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        boolean bl = logAtTrace = now < this.latestLog + this.gapTime && !finished;
        if (logAtTrace && !this.log.isTraceEnabled()) {
            return;
        }
        this.latestLog = now;
        String message = this.getFormatMessage(now);
        if (logAtTrace) {
            this.log.trace(message);
        } else {
            this.log.info(message);
        }
    }
}

