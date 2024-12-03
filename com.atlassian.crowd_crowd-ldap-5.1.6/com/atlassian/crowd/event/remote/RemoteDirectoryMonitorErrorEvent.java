/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.remote;

import com.atlassian.crowd.event.remote.RemoteDirectoryEvent;

public class RemoteDirectoryMonitorErrorEvent
extends RemoteDirectoryEvent {
    private final Exception exception;

    public RemoteDirectoryMonitorErrorEvent(Object source, long directoryID, Exception e) {
        super(source, directoryID);
        this.exception = e;
    }

    public Exception getException() {
        return this.exception;
    }
}

