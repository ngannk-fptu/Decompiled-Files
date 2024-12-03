/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.user.UserSignupEvent;
import com.atlassian.user.User;

public class PublicUserSignupEvent
extends UserSignupEvent {
    private static final long serialVersionUID = -7761854643254698171L;

    public PublicUserSignupEvent(Object src, User user) {
        super(src, user);
    }
}

