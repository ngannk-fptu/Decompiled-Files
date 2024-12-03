/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.api.event;

import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionStage;
import com.atlassian.confluence.plugin.copyspace.api.event.FailureReason;

public class ExecutionFailureDescriptor {
    private final ExecutionStage stage;
    private final FailureReason reason;

    public ExecutionFailureDescriptor(ExecutionStage stage, FailureReason reason) {
        this.stage = stage;
        this.reason = reason;
    }

    public ExecutionStage getStage() {
        return this.stage;
    }

    public FailureReason getReason() {
        return this.reason;
    }

    public static ExecutionFailureDescriptor unknown() {
        return new ExecutionFailureDescriptor(ExecutionStage.UNKNOWN, FailureReason.UNKNOWN);
    }
}

