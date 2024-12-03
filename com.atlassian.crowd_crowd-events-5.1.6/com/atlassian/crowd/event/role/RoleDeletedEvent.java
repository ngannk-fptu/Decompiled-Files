/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.event.role;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;

@Deprecated
public class RoleDeletedEvent
extends DirectoryEvent {
    private final String roleName;

    public RoleDeletedEvent(Object source, Directory directory, String roleName) {
        super(source, directory);
        this.roleName = roleName;
    }

    public String getRoleName() {
        return this.roleName;
    }
}

