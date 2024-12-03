/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.user.User;

public class AdminAddedUserEvent {
    private User user;

    public AdminAddedUserEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}

