/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.user.User;

public class DomainRestrictedUserSignupEvent
extends UserEvent {
    private static final long serialVersionUID = -4502242345506980892L;

    public DomainRestrictedUserSignupEvent(Object src, User user) {
        super(src, user);
    }
}

