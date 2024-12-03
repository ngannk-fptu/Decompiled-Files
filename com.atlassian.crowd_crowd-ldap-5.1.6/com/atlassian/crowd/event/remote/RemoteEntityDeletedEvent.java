/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.remote;

import com.atlassian.crowd.event.remote.RemoteDirectoryEvent;

public abstract class RemoteEntityDeletedEvent
extends RemoteDirectoryEvent {
    private final String entityName;

    protected RemoteEntityDeletedEvent(Object source, long directoryID, String entityName) {
        super(source, directoryID);
        this.entityName = entityName;
    }

    public String getEntityName() {
        return this.entityName;
    }
}

