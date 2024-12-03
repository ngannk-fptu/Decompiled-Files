/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.impl.TokenType;

class Token {
    private final TokenType tokenType;
    private final String debugString;
    private final ConfigOrigin origin;
    private final String tokenText;

    Token(TokenType tokenType, ConfigOrigin origin) {
        this(tokenType, origin, null);
    }

    Token(TokenType tokenType, ConfigOrigin origin, String tokenText) {
        this(tokenType, origin, tokenText, null);
    }

    Token(TokenType tokenType, ConfigOrigin origin, String tokenText, String debugString) {
        this.tokenType = tokenType;
        this.origin = origin;
        this.debugString = debugString;
        this.tokenText = tokenText;
    }

    static Token newWithoutOrigin(TokenType tokenType, String debugString, String tokenText) {
        return new Token(tokenType, null, tokenText, debugString);
    }

    final TokenType tokenType() {
        return this.tokenType;
    }

    public String tokenText() {
        return this.tokenText;
    }

    final ConfigOrigin origin() {
        if (this.origin == null) {
            throw new ConfigException.BugOrBroken("tried to get origin from token that doesn't have one: " + this);
        }
        return this.origin;
    }

    final int lineNumber() {
        if (this.origin != null) {
            return this.origin.lineNumber();
        }
        return -1;
    }

    public String toString() {
        if (this.debugString != null) {
            return this.debugString;
        }
        return this.tokenType.name();
    }

    protected boolean canEqual(Object other) {
        return other instanceof Token;
    }

    public boolean equals(Object other) {
        if (other instanceof Token) {
            return this.canEqual(other) && this.tokenType == ((Token)other).tokenType;
        }
        return false;
    }

    public int hashCode() {
        return this.tokenType.hashCode();
    }
}

