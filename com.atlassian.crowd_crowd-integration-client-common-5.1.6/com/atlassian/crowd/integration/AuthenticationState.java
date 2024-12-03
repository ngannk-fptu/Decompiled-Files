/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.integration;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import java.security.Principal;
import java.util.Objects;

public class AuthenticationState {
    private static final AuthenticationState AUTHENTICATED = new AuthenticationState(true, (Optional<Principal>)Optional.absent());
    private static final AuthenticationState UNAUTHENTICATED = new AuthenticationState(false, (Optional<Principal>)Optional.absent());
    private final boolean authenticated;
    private final Optional<Principal> authenticatedPrincipal;

    private AuthenticationState(boolean authenticated, Optional<Principal> authenticatedPrincipal) {
        this.authenticated = authenticated;
        this.authenticatedPrincipal = (Optional)Preconditions.checkNotNull(authenticatedPrincipal);
    }

    public static AuthenticationState authenticated(Principal principal) {
        return new AuthenticationState(true, (Optional<Principal>)Optional.of((Object)principal));
    }

    public static AuthenticationState authenticated() {
        return AUTHENTICATED;
    }

    public static AuthenticationState unauthenticated() {
        return UNAUTHENTICATED;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public Optional<Principal> getAuthenticatedPrincipal() {
        return this.authenticatedPrincipal;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuthenticationState that = (AuthenticationState)o;
        return Objects.equals(this.authenticated, that.authenticated) && Objects.equals(this.authenticatedPrincipal, that.authenticatedPrincipal);
    }

    public int hashCode() {
        return Objects.hash(this.authenticated, this.authenticatedPrincipal);
    }
}

