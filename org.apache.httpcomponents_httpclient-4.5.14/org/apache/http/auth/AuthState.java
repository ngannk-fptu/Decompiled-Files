/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.util.Args
 */
package org.apache.http.auth;

import java.util.Queue;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.util.Args;

public class AuthState {
    private AuthProtocolState state = AuthProtocolState.UNCHALLENGED;
    private AuthScheme authScheme;
    private AuthScope authScope;
    private Credentials credentials;
    private Queue<AuthOption> authOptions;

    public void reset() {
        this.state = AuthProtocolState.UNCHALLENGED;
        this.authOptions = null;
        this.authScheme = null;
        this.authScope = null;
        this.credentials = null;
    }

    public AuthProtocolState getState() {
        return this.state;
    }

    public void setState(AuthProtocolState state) {
        this.state = state != null ? state : AuthProtocolState.UNCHALLENGED;
    }

    public AuthScheme getAuthScheme() {
        return this.authScheme;
    }

    public Credentials getCredentials() {
        return this.credentials;
    }

    public void update(AuthScheme authScheme, Credentials credentials) {
        Args.notNull((Object)authScheme, (String)"Auth scheme");
        Args.notNull((Object)credentials, (String)"Credentials");
        this.authScheme = authScheme;
        this.credentials = credentials;
        this.authOptions = null;
    }

    public Queue<AuthOption> getAuthOptions() {
        return this.authOptions;
    }

    public boolean hasAuthOptions() {
        return this.authOptions != null && !this.authOptions.isEmpty();
    }

    public boolean isConnectionBased() {
        return this.authScheme != null && this.authScheme.isConnectionBased();
    }

    public void update(Queue<AuthOption> authOptions) {
        Args.notEmpty(authOptions, (String)"Queue of auth options");
        this.authOptions = authOptions;
        this.authScheme = null;
        this.credentials = null;
    }

    @Deprecated
    public void invalidate() {
        this.reset();
    }

    @Deprecated
    public boolean isValid() {
        return this.authScheme != null;
    }

    @Deprecated
    public void setAuthScheme(AuthScheme authScheme) {
        if (authScheme == null) {
            this.reset();
            return;
        }
        this.authScheme = authScheme;
    }

    @Deprecated
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    @Deprecated
    public AuthScope getAuthScope() {
        return this.authScope;
    }

    @Deprecated
    public void setAuthScope(AuthScope authScope) {
        this.authScope = authScope;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("state:").append((Object)this.state).append(";");
        if (this.authScheme != null) {
            buffer.append("auth scheme:").append(this.authScheme.getSchemeName()).append(";");
        }
        if (this.credentials != null) {
            buffer.append("credentials present");
        }
        return buffer.toString();
    }
}

