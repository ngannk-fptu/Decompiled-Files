/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security;

import java.security.Principal;

public interface AuthenticationContext {
    public Principal getPrincipal();

    public boolean isAuthenticated();
}

