/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.event;

import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.event.OperationEvent;
import java.util.Objects;
import javax.annotation.Nullable;

public abstract class AbstractOperationEvent
implements OperationEvent {
    private final Operation operation;
    private final Long directoryId;

    protected AbstractOperationEvent(Operation operation, @Nullable Long directoryId) {
        this.operation = operation;
        this.directoryId = directoryId;
    }

    @Override
    public Operation getOperation() {
        return this.operation;
    }

    @Override
    public Long getDirectoryId() {
        return this.directoryId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractOperationEvent that = (AbstractOperationEvent)o;
        return this.operation == that.operation && Objects.equals(this.directoryId, that.directoryId);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.operation, this.directoryId});
    }
}

