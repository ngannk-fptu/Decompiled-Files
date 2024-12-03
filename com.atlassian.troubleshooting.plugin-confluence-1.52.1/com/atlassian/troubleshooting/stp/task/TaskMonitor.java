/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.action.Message;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;

public interface TaskMonitor<V>
extends Future<V>,
Serializable {
    @Nonnull
    public List<Message> getErrors();

    @Nonnull
    public String getProgressMessage();

    public int getProgressPercentage();

    @Nonnull
    public String getTaskId();

    @Nonnull
    public List<Message> getWarnings();

    public Map<String, Serializable> getAttributes();

    public Optional<String> getNodeId();

    public Optional<String> getClusteredTaskId();

    public long getCreatedTimestamp();

    @Nonnull
    default public Status getStatus() {
        if (this.getProgressPercentage() < 100) {
            return Status.IN_PROGRESS;
        }
        if (!this.getErrors().isEmpty()) {
            return Status.FAILED;
        }
        if (!this.getWarnings().isEmpty()) {
            return Status.COMPLETE_WITH_WARNING;
        }
        return Status.SUCCESSFUL;
    }

    public static enum Status {
        IN_PROGRESS,
        COMPLETE_WITH_WARNING,
        FAILED,
        SUCCESSFUL;

    }
}

