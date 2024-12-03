/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.crowd.model.user.User;

public class DirectoryUserRenamedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 2993238183477552194L;
    private final String oldUsername;
    private final User user;

    public DirectoryUserRenamedEvent(Object src, String oldUsername, User user) {
        super(src);
        this.oldUsername = oldUsername;
        this.user = user;
    }

    public String getOldUsername() {
        return this.oldUsername;
    }

    public User getUser() {
        return this.user;
    }
}

