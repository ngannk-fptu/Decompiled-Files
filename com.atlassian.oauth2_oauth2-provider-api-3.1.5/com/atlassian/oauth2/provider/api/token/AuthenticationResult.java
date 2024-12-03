/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 */
package com.atlassian.oauth2.provider.api.token;

import com.atlassian.oauth2.scopes.api.Scope;

public class AuthenticationResult {
    private final Scope scope;
    private final String clientId;
    private final boolean authenticated;

    public static AuthenticationResult authenticated(Scope scope, String clientId) {
        return new AuthenticationResult(scope, clientId, true);
    }

    public static AuthenticationResult notAuthenticated() {
        return new AuthenticationResult(null, null, false);
    }

    private AuthenticationResult(Scope scope, String clientId, boolean authenticated) {
        this.scope = scope;
        this.clientId = clientId;
        this.authenticated = authenticated;
    }

    public Scope getScope() {
        return this.scope;
    }

    public String getClientId() {
        return this.clientId;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }
}

