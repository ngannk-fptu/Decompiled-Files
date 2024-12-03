/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.scopes.api;

import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.atlassian.oauth2.scopes.api.Scope;
import java.util.Set;

public interface ScopeResolver {
    public Scope getScope(String var1) throws InvalidScopeException;

    public boolean hasScopePermission(Scope var1, Scope var2);

    public Set<Scope> getAvailableScopes();
}

