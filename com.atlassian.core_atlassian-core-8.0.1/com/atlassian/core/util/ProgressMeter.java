/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

public class ProgressMeter {
    int percentageComplete;
    private String status;
    private int total;
    private int currentCount;
    private boolean completedSuccessfully = true;

    public void setPercentage(int count, int total) {
        if (total < 0) {
            this.setPercentage(0);
        } else if (total <= count) {
            this.setPercentage(100);
        } else {
            int calculatedPercentage = (int)(100.0f * (float)count / (float)total);
            if (count < total && calculatedPercentage == 100) {
                calculatedPercentage = 99;
            }
            this.setPercentage(calculatedPercentage);
        }
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public synchronized int getPercentageComplete() {
        return this.percentageComplete;
    }

    public synchronized String getStatus() {
        return this.status;
    }

    public synchronized void setPercentage(int percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public synchronized int getCurrentCount() {
        return this.currentCount;
    }

    public synchronized void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
        this.updatePercentageComplete();
    }

    private void updatePercentageComplete() {
        this.setPercentage(this.getCurrentCount(), this.getTotal());
    }

    public synchronized int getTotal() {
        return this.total;
    }

    public synchronized void setTotalObjects(int total) {
        this.total = total;
        this.updatePercentageComplete();
    }

    public synchronized boolean isCompletedSuccessfully() {
        return this.completedSuccessfully;
    }

    public synchronized void setCompletedSuccessfully(boolean completedSuccessfully) {
        this.completedSuccessfully = completedSuccessfully;
    }
}

