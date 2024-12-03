/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.retention;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.retention.RemovalSummary;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class SoftCleanupJobStatus {
    @JsonProperty
    private RemovalSummary overall = new RemovalSummary();
    @JsonProperty
    private RemovalSummary currentCycle = new RemovalSummary();
    @JsonProperty
    private RemovalSummary lastIteration = new RemovalSummary();
    @JsonProperty
    private int cyclesCompleted = 0;
    @JsonProperty
    private int iterationsCompleted = 0;
    @JsonProperty
    private long nextStartOriginalId = 0L;

    public RemovalSummary getOverall() {
        return this.overall;
    }

    public void setOverall(RemovalSummary overall) {
        this.overall = overall;
    }

    public RemovalSummary getLastIteration() {
        return this.lastIteration;
    }

    public void setLastIteration(RemovalSummary lastIteration) {
        this.lastIteration = lastIteration;
    }

    public RemovalSummary getCurrentCycle() {
        return this.currentCycle;
    }

    public void setCurrentCycle(RemovalSummary currentCycle) {
        this.currentCycle = currentCycle;
    }

    public int getIterationsCompleted() {
        return this.iterationsCompleted;
    }

    public void setIterationsCompleted(int iterationsCompleted) {
        this.iterationsCompleted = iterationsCompleted;
    }

    public int incrementIterationsCompleted() {
        return ++this.iterationsCompleted;
    }

    public long getNextStartOriginalId() {
        return this.nextStartOriginalId;
    }

    public void setNextStartOriginalId(long nextStartOriginalId) {
        this.nextStartOriginalId = nextStartOriginalId;
    }

    public int getCyclesCompleted() {
        return this.cyclesCompleted;
    }

    public void setCyclesCompleted(int cyclesCompleted) {
        this.cyclesCompleted = cyclesCompleted;
    }

    public int incrementCycleCount() {
        return ++this.cyclesCompleted;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SoftCleanupJobStatus)) {
            return false;
        }
        SoftCleanupJobStatus that = (SoftCleanupJobStatus)obj;
        return Objects.equals(this.overall, that.overall) && Objects.equals(this.currentCycle, that.currentCycle) && Objects.equals(this.iterationsCompleted, that.iterationsCompleted) && Objects.equals(this.cyclesCompleted, that.cyclesCompleted) && Objects.equals(this.lastIteration, that.lastIteration) && Objects.equals(this.nextStartOriginalId, that.nextStartOriginalId);
    }

    public int hashCode() {
        return Objects.hash(this.overall, this.currentCycle, this.iterationsCompleted, this.cyclesCompleted, this.lastIteration, this.nextStartOriginalId);
    }

    public String toString() {
        return "SoftCleanupJobStatus{overall=" + this.overall + ", currentCycle=" + this.currentCycle + ", lastIteration=" + this.lastIteration + ", cycles=" + this.cyclesCompleted + ", iterationsCompleted=" + this.iterationsCompleted + ", nextStartOriginalId=" + this.nextStartOriginalId + '}';
    }
}

