/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.user.User;

public abstract class UserSignupEvent
extends UserEvent {
    private static final long serialVersionUID = -1692096854806809440L;

    public UserSignupEvent(Object src, User user) {
        super(src, user);
    }
}

