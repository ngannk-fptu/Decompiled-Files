/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.expression.spel.standard;

import org.springframework.expression.spel.standard.TokenKind;
import org.springframework.lang.Nullable;

class Token {
    TokenKind kind;
    @Nullable
    String data;
    int startPos;
    int endPos;

    Token(TokenKind tokenKind, int startPos, int endPos) {
        this.kind = tokenKind;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    Token(TokenKind tokenKind, char[] tokenData, int startPos, int endPos) {
        this(tokenKind, startPos, endPos);
        this.data = new String(tokenData);
    }

    public TokenKind getKind() {
        return this.kind;
    }

    public boolean isIdentifier() {
        return this.kind == TokenKind.IDENTIFIER;
    }

    public boolean isNumericRelationalOperator() {
        return this.kind == TokenKind.GT || this.kind == TokenKind.GE || this.kind == TokenKind.LT || this.kind == TokenKind.LE || this.kind == TokenKind.EQ || this.kind == TokenKind.NE;
    }

    public String stringValue() {
        return this.data != null ? this.data : "";
    }

    public Token asInstanceOfToken() {
        return new Token(TokenKind.INSTANCEOF, this.startPos, this.endPos);
    }

    public Token asMatchesToken() {
        return new Token(TokenKind.MATCHES, this.startPos, this.endPos);
    }

    public Token asBetweenToken() {
        return new Token(TokenKind.BETWEEN, this.startPos, this.endPos);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append((Object)this.kind);
        if (this.kind.hasPayload()) {
            sb.append(':').append(this.data);
        }
        sb.append(']');
        sb.append('(').append(this.startPos).append(',').append(this.endPos).append(')');
        return sb.toString();
    }
}

