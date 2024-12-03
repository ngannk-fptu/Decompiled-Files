/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.expression;

import org.springframework.security.core.Authentication;

public interface SecurityExpressionOperations {
    public Authentication getAuthentication();

    public boolean hasAuthority(String var1);

    public boolean hasAnyAuthority(String ... var1);

    public boolean hasRole(String var1);

    public boolean hasAnyRole(String ... var1);

    public boolean permitAll();

    public boolean denyAll();

    public boolean isAnonymous();

    public boolean isAuthenticated();

    public boolean isRememberMe();

    public boolean isFullyAuthenticated();

    public boolean hasPermission(Object var1, Object var2);

    public boolean hasPermission(Object var1, String var2, Object var3);
}

