/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.event.role;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.model.group.Group;

@Deprecated
public class RoleUpdatedEvent
extends DirectoryEvent {
    private final Group role;

    public RoleUpdatedEvent(Object source, Directory directory, Group role) {
        super(source, directory);
        this.role = role;
    }

    public Group getRole() {
        return this.role;
    }
}

