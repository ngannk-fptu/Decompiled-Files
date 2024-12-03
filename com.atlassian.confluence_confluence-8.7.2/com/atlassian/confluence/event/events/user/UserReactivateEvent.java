/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.user.User;

public class UserReactivateEvent
extends UserEvent
implements Updated {
    private static final long serialVersionUID = -6601670645559513892L;
    private final boolean suppressWebhook;

    public UserReactivateEvent(Object src, User user) {
        this(src, user, false);
    }

    public UserReactivateEvent(Object source, User user, boolean suppressWebhook) {
        super(source, user);
        this.suppressWebhook = suppressWebhook;
    }

    public boolean isSuppressWebhook() {
        return this.suppressWebhook;
    }
}

