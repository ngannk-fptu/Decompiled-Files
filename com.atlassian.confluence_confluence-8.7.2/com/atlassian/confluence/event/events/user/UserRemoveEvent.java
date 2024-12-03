/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.user.User;

public class UserRemoveEvent
extends UserEvent
implements Removed {
    private static final long serialVersionUID = -3026118305656390016L;

    public UserRemoveEvent(Object src, User user) {
        super(src, user);
    }
}

