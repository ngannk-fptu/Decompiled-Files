/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import com.atlassian.core.util.ProgressMeter;

public class ProgressWrapper {
    private final ProgressMeter progressMeter;
    private final int totalCount;
    private int currentCount;

    public ProgressWrapper(ProgressMeter progressMeter, int totalCount) {
        this.progressMeter = progressMeter;
        this.totalCount = totalCount;
        progressMeter.setTotalObjects(totalCount);
        this.currentCount = 0;
    }

    public synchronized void incrementCounter() {
        ++this.currentCount;
        this.progressMeter.setPercentage(this.currentCount, this.totalCount);
    }

    public synchronized void incrementCounter(String status) {
        ++this.currentCount;
        this.progressMeter.setPercentage(this.currentCount, this.totalCount);
        this.progressMeter.setStatus(status);
    }

    public synchronized void setStatus(String status) {
        this.progressMeter.setStatus(status);
    }

    public synchronized int getTotal() {
        return this.progressMeter.getTotal();
    }

    public synchronized void setPercentage(int percentageComplete) {
        this.progressMeter.setPercentage(percentageComplete);
    }

    public String progressAsString() {
        return this.currentCount + " of " + this.totalCount + " total objects.";
    }
}

