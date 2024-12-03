/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.model.token;

import com.google.common.base.Preconditions;

public class TokenLifetime {
    private static final long DEFAULT_VALUE = -1L;
    private final long seconds;
    public static final TokenLifetime USE_DEFAULT = new TokenLifetime();

    private TokenLifetime() {
        this.seconds = -1L;
    }

    private TokenLifetime(long seconds) {
        Preconditions.checkArgument((seconds >= 0L ? 1 : 0) != 0, (String)"The duration %s must be greater or equal to 0", (long)seconds);
        this.seconds = seconds;
    }

    public long getSeconds() {
        Preconditions.checkState((this.seconds >= 0L ? 1 : 0) != 0, (Object)"Default lifetime does not prescribe a particular duration");
        return this.seconds;
    }

    public boolean isDefault() {
        return this.seconds == -1L;
    }

    public static TokenLifetime inSeconds(long seconds) {
        return new TokenLifetime(seconds);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TokenLifetime that = (TokenLifetime)o;
        return this.seconds == that.seconds;
    }

    public int hashCode() {
        return (int)(this.seconds ^ this.seconds >>> 32);
    }
}

