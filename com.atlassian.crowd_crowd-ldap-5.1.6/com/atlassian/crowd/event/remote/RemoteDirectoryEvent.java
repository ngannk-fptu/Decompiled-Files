/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.Event
 */
package com.atlassian.crowd.event.remote;

import com.atlassian.crowd.event.Event;

public abstract class RemoteDirectoryEvent
extends Event {
    private final long directoryId;

    protected RemoteDirectoryEvent(Object source, long directoryId) {
        super(source);
        this.directoryId = directoryId;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }
}

