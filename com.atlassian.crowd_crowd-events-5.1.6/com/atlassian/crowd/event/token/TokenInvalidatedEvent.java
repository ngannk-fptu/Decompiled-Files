/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.token.Token
 */
package com.atlassian.crowd.event.token;

import com.atlassian.crowd.event.Event;
import com.atlassian.crowd.model.token.Token;

public class TokenInvalidatedEvent
extends Event {
    private final Token token;

    public TokenInvalidatedEvent(Object source, Token token) {
        super(source);
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }
}

