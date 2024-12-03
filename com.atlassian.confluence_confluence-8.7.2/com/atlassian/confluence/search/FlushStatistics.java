/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search;

import java.io.Serializable;
import java.util.Date;

public class FlushStatistics
implements Serializable {
    private static final long serialVersionUID = 1446270176651260001L;
    private Date started;
    private Date finished;
    private long queueSize;
    private boolean recreated;

    protected void reset() {
        this.started = null;
        this.finished = null;
        this.queueSize = 0L;
        this.recreated = false;
    }

    public long getElapsedMilliseconds() {
        if (this.started == null) {
            return 0L;
        }
        if (this.finished == null) {
            return System.currentTimeMillis() - this.started.getTime();
        }
        return this.finished.getTime() - this.started.getTime();
    }

    public long getQueueSize() {
        return this.queueSize;
    }

    public boolean wasRecreated() {
        return this.recreated;
    }

    public Date getStarted() {
        return this.started;
    }

    public Date getFinished() {
        return this.finished;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }

    public void setRecreated(boolean recreated) {
        this.recreated = recreated;
    }
}

