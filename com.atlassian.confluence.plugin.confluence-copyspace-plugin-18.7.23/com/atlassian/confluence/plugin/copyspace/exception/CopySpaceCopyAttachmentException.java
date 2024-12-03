/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.exception;

import com.atlassian.confluence.plugin.copyspace.api.event.ExecutionFailureDescriptor;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceFlowException;

public class CopySpaceCopyAttachmentException
extends CopySpaceFlowException {
    public CopySpaceCopyAttachmentException(ExecutionFailureDescriptor failureDescriptor, Throwable cause) {
        super(failureDescriptor, cause);
    }
}

