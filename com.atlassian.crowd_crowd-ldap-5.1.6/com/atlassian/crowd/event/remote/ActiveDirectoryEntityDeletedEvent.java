/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.remote;

import com.atlassian.crowd.event.remote.RemoteDirectoryEvent;
import com.atlassian.crowd.model.Tombstone;

public abstract class ActiveDirectoryEntityDeletedEvent
extends RemoteDirectoryEvent {
    private final Tombstone tombstone;

    public ActiveDirectoryEntityDeletedEvent(Object source, long directoryID, Tombstone tombstone) {
        super(source, directoryID);
        this.tombstone = tombstone;
    }

    public Tombstone getTombstone() {
        return this.tombstone;
    }
}

