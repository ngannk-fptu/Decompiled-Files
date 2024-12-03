/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.event;

import com.atlassian.crowd.model.event.Operation;
import javax.annotation.Nullable;

public interface OperationEvent {
    public Operation getOperation();

    @Nullable
    public Long getDirectoryId();
}

