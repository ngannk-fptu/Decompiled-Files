/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.auth;

import java.security.Principal;

public interface AuthenticationContext {
    public Principal getUser();

    public void setUser(Principal var1);

    public void clearUser();
}

