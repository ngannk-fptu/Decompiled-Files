/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.crowd.event.remote.principal;

import com.atlassian.crowd.event.remote.RemoteEntityCreatedOrUpdatedEvent;
import com.atlassian.crowd.event.remote.principal.RemoteUserEvent;
import com.atlassian.crowd.model.user.User;

public class RemoteUserCreatedOrUpdatedEvent
extends RemoteEntityCreatedOrUpdatedEvent<User>
implements RemoteUserEvent {
    public RemoteUserCreatedOrUpdatedEvent(Object source, long directoryID, User user) {
        super(source, directoryID, user);
    }
}

