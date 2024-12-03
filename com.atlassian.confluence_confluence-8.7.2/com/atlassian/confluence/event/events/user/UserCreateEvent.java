/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.user.User;

public class UserCreateEvent
extends UserEvent
implements Created {
    private static final long serialVersionUID = 8020934508478889698L;

    public UserCreateEvent(Object src, User user) {
        super(src, user);
    }
}

