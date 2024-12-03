/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.auth;

import com.atlassian.seraph.auth.AuthenticationContext;
import java.security.Principal;

public class AuthenticationContextImpl
implements AuthenticationContext {
    private static final ThreadLocal<Principal> threadLocal = new ThreadLocal();

    @Override
    public Principal getUser() {
        return threadLocal.get();
    }

    @Override
    public void setUser(Principal user) {
        threadLocal.set(user);
    }

    @Override
    public void clearUser() {
        this.setUser(null);
    }
}

