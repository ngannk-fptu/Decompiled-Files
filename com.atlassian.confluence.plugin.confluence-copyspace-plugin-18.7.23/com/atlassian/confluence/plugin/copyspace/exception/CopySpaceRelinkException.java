/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.exception;

import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionFailureDescriptor;
import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionStage;
import com.atlassian.confluence.plugin.copyspace.api.event.FailureReason;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceFlowException;

public class CopySpaceRelinkException
extends CopySpaceFlowException {
    public CopySpaceRelinkException(Throwable cause) {
        super(new ExecutionFailureDescriptor(ExecutionStage.RELINK, FailureReason.UNABLE_TO_REWRITE_LINK), cause);
    }

    public CopySpaceRelinkException(ExecutionFailureDescriptor failureDescriptor, Throwable cause) {
        super(failureDescriptor, cause);
    }
}

