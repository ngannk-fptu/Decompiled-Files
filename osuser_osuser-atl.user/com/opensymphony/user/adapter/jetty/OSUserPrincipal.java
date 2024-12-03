/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.UserPrincipal
 */
package com.opensymphony.user.adapter.jetty;

import com.opensymphony.user.User;
import org.mortbay.http.UserPrincipal;

public class OSUserPrincipal
implements UserPrincipal {
    private User user;
    private boolean auth;

    public OSUserPrincipal(User user, String password) {
        this.user = user;
        this.auth = user.authenticate(password);
    }

    public boolean isAuthenticated() {
        return this.auth;
    }

    public String getName() {
        return this.user.getName();
    }

    public boolean isUserInRole(String group) {
        return this.user.inGroup(group);
    }
}

