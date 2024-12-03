/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.exception;

import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionFailureDescriptor;

public class CopySpaceFlowException
extends RuntimeException {
    private final ExecutionFailureDescriptor failureDescriptor;

    public CopySpaceFlowException(ExecutionFailureDescriptor failureDescriptor, Throwable cause) {
        super(cause);
        this.failureDescriptor = failureDescriptor;
    }

    public ExecutionFailureDescriptor getFailureDescriptor() {
        return this.failureDescriptor;
    }
}

