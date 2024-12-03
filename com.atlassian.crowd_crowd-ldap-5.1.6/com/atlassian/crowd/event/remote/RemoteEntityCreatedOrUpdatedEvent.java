/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.DirectoryEntity
 */
package com.atlassian.crowd.event.remote;

import com.atlassian.crowd.event.remote.RemoteDirectoryEvent;
import com.atlassian.crowd.model.DirectoryEntity;

public abstract class RemoteEntityCreatedOrUpdatedEvent<T extends DirectoryEntity>
extends RemoteDirectoryEvent {
    private final T entity;

    protected RemoteEntityCreatedOrUpdatedEvent(Object source, long directoryID, T entity) {
        super(source, directoryID);
        this.entity = entity;
    }

    public T getEntity() {
        return this.entity;
    }
}

