/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.index.status;

import com.atlassian.confluence.index.status.ReIndexError;
import com.atlassian.confluence.index.status.ReIndexJob;
import java.io.Serializable;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ReIndexNodeStatus
implements Serializable {
    private String nodeId;
    private State state;
    private ReIndexError error;
    private ReIndexJob.Progress progress;

    public ReIndexNodeStatus() {
    }

    public ReIndexNodeStatus(String nodeId, State state) {
        this(nodeId, state, null);
    }

    public ReIndexNodeStatus(String nodeId, State state, @Nullable ReIndexError error) {
        this.nodeId = Objects.requireNonNull(nodeId);
        this.state = Objects.requireNonNull(state);
        this.error = error;
        this.progress = new ReIndexJob.Progress();
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public @Nullable ReIndexError getError() {
        return this.error;
    }

    public void setError(ReIndexError error) {
        this.error = error;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ReIndexNodeStatus that = (ReIndexNodeStatus)o;
        return this.nodeId.equals(that.nodeId) && this.state == that.state && this.error == that.error;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.nodeId, this.state, this.error});
    }

    public String toString() {
        return String.format("Node Id: %s. State: %s. %s.", new Object[]{this.nodeId, this.state, this.error == null ? "No error recorded" : "Error:" + this.error.name()});
    }

    public void setProgress(ReIndexJob.Progress progress) {
        this.progress = progress;
    }

    public ReIndexJob.Progress getProgress() {
        return this.progress;
    }

    public boolean isFailed() {
        return this.state != null && this.state.isFailed();
    }

    public boolean isComplete() {
        return this.state != null && this.state.isComplete();
    }

    public boolean isFinished() {
        return this.isComplete() || this.isFailed();
    }

    public static enum State {
        WAITING,
        REBUILDING,
        REBUILD_COMPLETE,
        REBUILD_FAILED,
        REBUILD_SKIPPED,
        PROPAGATING,
        PROPAGATION_FAIL,
        PROPAGATION_COMPLETE,
        PROPAGATION_SKIPPED,
        UNAVAILABLE;


        public boolean isFailed() {
            return this == REBUILD_FAILED || this == PROPAGATION_FAIL || this == UNAVAILABLE;
        }

        public boolean isComplete() {
            return this == REBUILD_COMPLETE || this == PROPAGATION_COMPLETE || this == REBUILD_SKIPPED || this == PROPAGATION_SKIPPED;
        }

        public boolean isFinished() {
            return this.isComplete() || this.isFailed();
        }
    }
}

