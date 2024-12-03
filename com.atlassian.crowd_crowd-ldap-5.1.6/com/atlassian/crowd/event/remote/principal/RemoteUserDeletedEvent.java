/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.remote.principal;

import com.atlassian.crowd.event.remote.RemoteEntityDeletedEvent;
import com.atlassian.crowd.event.remote.principal.RemoteUserEvent;

public class RemoteUserDeletedEvent
extends RemoteEntityDeletedEvent
implements RemoteUserEvent {
    public RemoteUserDeletedEvent(Object source, long directoryID, String username) {
        super(source, directoryID, username);
    }
}

