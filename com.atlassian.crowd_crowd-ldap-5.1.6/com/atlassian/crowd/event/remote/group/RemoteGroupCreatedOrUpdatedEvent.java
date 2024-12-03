/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.event.remote.group;

import com.atlassian.crowd.event.remote.RemoteEntityCreatedOrUpdatedEvent;
import com.atlassian.crowd.event.remote.group.RemoteGroupEvent;
import com.atlassian.crowd.model.group.Group;

public class RemoteGroupCreatedOrUpdatedEvent
extends RemoteEntityCreatedOrUpdatedEvent<Group>
implements RemoteGroupEvent {
    public RemoteGroupCreatedOrUpdatedEvent(Object source, long directoryID, Group group) {
        super(source, directoryID, group);
    }
}

