/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.auth;

import java.util.Queue;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.core5.util.Args;

public class AuthExchange {
    private State state = State.UNCHALLENGED;
    private AuthScheme authScheme;
    private Queue<AuthScheme> authOptions;
    private String pathPrefix;

    public void reset() {
        this.state = State.UNCHALLENGED;
        this.authOptions = null;
        this.authScheme = null;
        this.pathPrefix = null;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state != null ? state : State.UNCHALLENGED;
    }

    public AuthScheme getAuthScheme() {
        return this.authScheme;
    }

    public boolean isConnectionBased() {
        return this.authScheme != null && this.authScheme.isConnectionBased();
    }

    public String getPathPrefix() {
        return this.pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public void select(AuthScheme authScheme) {
        Args.notNull((Object)authScheme, (String)"Auth scheme");
        this.authScheme = authScheme;
        this.authOptions = null;
    }

    public Queue<AuthScheme> getAuthOptions() {
        return this.authOptions;
    }

    public void setOptions(Queue<AuthScheme> authOptions) {
        Args.notEmpty(authOptions, (String)"Queue of auth options");
        this.authOptions = authOptions;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[").append((Object)this.state);
        if (this.authScheme != null) {
            buffer.append(" ").append(this.authScheme);
        }
        buffer.append("]");
        return buffer.toString();
    }

    public static enum State {
        UNCHALLENGED,
        CHALLENGED,
        HANDSHAKE,
        FAILURE,
        SUCCESS;

    }
}

