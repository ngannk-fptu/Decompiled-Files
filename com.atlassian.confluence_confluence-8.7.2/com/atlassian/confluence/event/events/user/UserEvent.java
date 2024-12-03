/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.user.User;

public abstract class UserEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -2994046617034844938L;
    private final User user;

    public UserEvent(Object src, User user) {
        super(src);
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}

