/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.core.util.ProgressMeter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogProgressMeterWrapper
extends ProgressMeter {
    private static final Logger log = LoggerFactory.getLogger(LogProgressMeterWrapper.class);
    private ProgressMeter progressMeter;

    public LogProgressMeterWrapper(ProgressMeter progressMeter) {
        this.progressMeter = progressMeter;
    }

    public void setPercentage(int count, int total) {
        this.progressMeter.setPercentage(count, total);
    }

    public synchronized void setStatus(String status) {
        log.debug(status);
        this.progressMeter.setStatus(status);
    }

    public synchronized int getPercentageComplete() {
        return this.progressMeter.getPercentageComplete();
    }

    public synchronized String getStatus() {
        return this.progressMeter.getStatus();
    }

    public synchronized int getCurrentCount() {
        return this.progressMeter.getCurrentCount();
    }

    public synchronized void setCurrentCount(int currentCount) {
        this.progressMeter.setCurrentCount(currentCount);
    }

    public synchronized int getTotal() {
        return this.progressMeter.getTotal();
    }

    public synchronized void setTotalObjects(int total) {
        this.progressMeter.setTotalObjects(total);
    }

    public synchronized boolean isCompletedSuccessfully() {
        return this.progressMeter.isCompletedSuccessfully();
    }

    public synchronized void setCompletedSuccessfully(boolean completedSuccessfully) {
        this.progressMeter.setCompletedSuccessfully(completedSuccessfully);
        if (completedSuccessfully) {
            log.debug("Bulk Copy Successfully Complete");
        }
    }

    public synchronized void setPercentage(int percentageComplete) {
        this.progressMeter.setPercentage(percentageComplete);
        log.debug("Bulk copy current progress: {}%", (Object)percentageComplete);
    }
}

