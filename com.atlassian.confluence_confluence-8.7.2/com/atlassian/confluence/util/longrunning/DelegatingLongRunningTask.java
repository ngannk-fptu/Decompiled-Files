/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.core.task.longrunning.LongRunningTask;

public abstract class DelegatingLongRunningTask
implements LongRunningTask {
    protected final LongRunningTask delegate;

    protected DelegatingLongRunningTask(LongRunningTask delegate) {
        this.delegate = delegate;
    }

    public int getPercentageComplete() {
        return this.delegate.getPercentageComplete();
    }

    public String getName() {
        return this.delegate.getName();
    }

    public String getNameKey() {
        return this.delegate.getNameKey();
    }

    public String getCurrentStatus() {
        return this.delegate.getCurrentStatus();
    }

    public long getElapsedTime() {
        return this.delegate.getElapsedTime();
    }

    public String getPrettyElapsedTime() {
        return this.delegate.getPrettyElapsedTime();
    }

    public long getEstimatedTimeRemaining() {
        return this.delegate.getEstimatedTimeRemaining();
    }

    public String getPrettyTimeRemaining() {
        return this.delegate.getPrettyTimeRemaining();
    }

    public boolean isComplete() {
        return this.delegate.isComplete();
    }

    public boolean isSuccessful() {
        return this.delegate.isSuccessful();
    }

    public void run() {
        this.delegate.run();
    }
}

