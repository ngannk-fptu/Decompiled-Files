/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.token;

import java.util.Date;
import java.util.Objects;

final class Token {
    private final String token;
    private final Date creationDate;

    Token(String token, Date creationDate) {
        this.token = Objects.requireNonNull(token, "token");
        this.creationDate = Objects.requireNonNull(creationDate, "creationDate");
    }

    public String getValue() {
        return this.token;
    }

    public boolean isExpired() {
        Date tokenExpiryDate = new Date(this.creationDate.getTime() + 300000L);
        Date currentDate = new Date();
        return currentDate.after(tokenExpiryDate);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Token otherToken = (Token)o;
        return this.token.equals(otherToken.token) && this.creationDate.equals(otherToken.creationDate);
    }

    public int hashCode() {
        return this.token.hashCode();
    }

    public String toString() {
        return this.token;
    }
}

