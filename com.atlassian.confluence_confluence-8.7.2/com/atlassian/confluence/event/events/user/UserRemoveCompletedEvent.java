/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.user.User;

@AsynchronousPreferred
public class UserRemoveCompletedEvent
extends UserEvent
implements Removed {
    private static final long serialVersionUID = 9150559484050996543L;

    public UserRemoveCompletedEvent(Object src, User user) {
        super(src, user);
    }
}

