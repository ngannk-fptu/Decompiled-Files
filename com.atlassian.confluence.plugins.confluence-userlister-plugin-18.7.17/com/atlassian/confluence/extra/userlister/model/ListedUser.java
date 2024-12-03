/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.extra.userlister.model;

import com.atlassian.user.User;

public class ListedUser {
    private User baseUser;
    private boolean loggedIn;

    public ListedUser(User user, boolean loggedIn) {
        this.baseUser = user;
        this.loggedIn = loggedIn;
    }

    public String getFullName() {
        return this.baseUser.getFullName();
    }

    public String getName() {
        return this.baseUser.getName();
    }

    public String getEmail() {
        return this.baseUser.getEmail();
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public boolean getLoggedIn() {
        return this.loggedIn;
    }
}

