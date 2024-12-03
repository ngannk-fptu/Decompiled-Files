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

public class UserDeactivateEvent
extends UserEvent
implements Updated {
    private static final long serialVersionUID = 3045482946419493262L;
    private final boolean suppressWebhook;

    public UserDeactivateEvent(Object source, User user) {
        this(source, user, false);
    }

    public UserDeactivateEvent(Object source, User user, boolean suppressWebhook) {
        super(source, user);
        this.suppressWebhook = suppressWebhook;
    }

    public boolean isSuppressWebhook() {
        return this.suppressWebhook;
    }
}

