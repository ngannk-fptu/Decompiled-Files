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
public class RoleMembershipDeletedEvent
extends DirectoryEvent {
    private final String username;
    private final String roleName;

    public RoleMembershipDeletedEvent(Object source, Directory directory, String username, String roleName) {
        super(source, directory);
        this.username = username;
        this.roleName = roleName;
    }

    public String getUsername() {
        return this.username;
    }

    public String getRoleName() {
        return this.roleName;
    }
}

