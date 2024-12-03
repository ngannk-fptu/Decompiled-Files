/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

public class EstimatedProgressMeter {
    private int estimatedTotal;
    private int currentCount;
    private int percentageComplete;

    public EstimatedProgressMeter(int estimatedTotal) {
        if (estimatedTotal < 0) {
            throw new IllegalArgumentException("estimatedTotal cannot be less than zero.");
        }
        this.estimatedTotal = estimatedTotal;
        this.currentCount = 0;
        this.percentageComplete = 0;
    }

    public synchronized int incrementCount() {
        ++this.currentCount;
        if (this.currentCount <= this.estimatedTotal && this.estimatedTotal > 0) {
            this.percentageComplete = (int)(100.0f * (float)this.currentCount / (float)this.estimatedTotal);
            if (this.percentageComplete == 100) {
                this.percentageComplete = 99;
            }
        }
        return this.currentCount;
    }

    public synchronized int getPercentageComplete() {
        return this.percentageComplete;
    }

    public synchronized void complete() {
        this.percentageComplete = 100;
    }

    public synchronized int getCount() {
        return this.currentCount;
    }

    public synchronized int getEstimatedTotal() {
        return this.estimatedTotal;
    }

    public synchronized void setEstimatedTotal(int estimatedTotal) {
        if (estimatedTotal < 0) {
            throw new IllegalArgumentException("estimatedTotal cannot be less than zero.");
        }
        if (this.currentCount > 0) {
            throw new IllegalStateException("You are not permitted to change the estimatedTotal once progress has started.");
        }
        this.estimatedTotal = estimatedTotal;
    }
}

