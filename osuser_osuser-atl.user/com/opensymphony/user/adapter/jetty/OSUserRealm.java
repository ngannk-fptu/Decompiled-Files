/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mortbay.http.HttpRequest
 *  org.mortbay.http.UserPrincipal
 *  org.mortbay.http.UserRealm
 */
package com.opensymphony.user.adapter.jetty;

import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.user.adapter.jetty.OSUserPrincipal;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.UserPrincipal;
import org.mortbay.http.UserRealm;

public class OSUserRealm
implements UserRealm {
    private UserManager um = UserManager.getInstance();

    public String getName() {
        return "OSUser-Jetty adapater";
    }

    public UserPrincipal authenticate(String username, Object password, HttpRequest httpRequest) {
        return this.authenticate(username, (String)password, httpRequest);
    }

    public UserPrincipal authenticate(String username, String password, HttpRequest httpRequest) {
        try {
            User user = this.um.getUser(username);
            return new OSUserPrincipal(user, password);
        }
        catch (EntityNotFoundException e) {
            return null;
        }
    }

    public void disassociate(UserPrincipal userPrincipal) {
        throw new UnsupportedOperationException();
    }

    public void dissassociate(UserPrincipal userPrincipal) {
    }

    public UserPrincipal popRole(UserPrincipal userPrincipal) {
        return null;
    }

    public UserPrincipal pushRole(UserPrincipal userPrincipal, String role) {
        return null;
    }
}

