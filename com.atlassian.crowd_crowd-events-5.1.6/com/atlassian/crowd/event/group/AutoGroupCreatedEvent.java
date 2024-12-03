/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.event.group;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.group.GroupCreatedEvent;
import com.atlassian.crowd.model.group.Group;

public class AutoGroupCreatedEvent
extends GroupCreatedEvent {
    public AutoGroupCreatedEvent(Object source, Directory directory, Group group) {
        super(source, directory, group);
    }
}

