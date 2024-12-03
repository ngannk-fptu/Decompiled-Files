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
public class RoleMembershipCreatedEvent
extends DirectoryEvent {
    private final String username;
    private final String rolename;

    public RoleMembershipCreatedEvent(Object source, Directory directory, String username, String rolename) {
        super(source, directory);
        this.username = username;
        this.rolename = rolename;
    }

    public String getUsername() {
        return this.username;
    }

    public String getRolename() {
        return this.rolename;
    }
}

