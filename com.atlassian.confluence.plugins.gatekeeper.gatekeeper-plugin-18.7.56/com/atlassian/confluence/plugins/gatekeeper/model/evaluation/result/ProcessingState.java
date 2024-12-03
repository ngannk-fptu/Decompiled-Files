/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result;

import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.ProcessingPhase;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProcessingState {
    private ProcessingPhase phase;
    private int percent;
    @JsonIgnore
    private long expectedEndTime;

    public void setExpectedEndTime(long expectedEndTime) {
        this.expectedEndTime = expectedEndTime;
    }

    public ProcessingState() {
        this(ProcessingPhase.INIT);
    }

    public ProcessingState(ProcessingPhase phase) {
        this.phase = phase;
    }

    public ProcessingPhase getPhase() {
        return this.phase;
    }

    public void setPhase(ProcessingPhase phase) {
        this.phase = phase;
    }

    public int getPercent() {
        return this.percent;
    }

    public void setPercent(long percent) {
        this.percent = Math.min((int)percent, 100);
    }

    public boolean isDone() {
        return this.phase == ProcessingPhase.DONE;
    }

    public boolean isReady() {
        return this.phase != ProcessingPhase.FAILED;
    }

    public boolean isTimedOut() {
        return this.phase == ProcessingPhase.TIMED_OUT;
    }

    @JsonIgnore
    public boolean isExpired() {
        return System.currentTimeMillis() > this.expectedEndTime;
    }
}

