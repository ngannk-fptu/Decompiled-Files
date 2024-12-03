/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.user;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class UserKey
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String userkey;

    public UserKey(@Nonnull String userkey) {
        Objects.requireNonNull(userkey, "userkey");
        this.userkey = userkey;
    }

    @Nonnull
    public String getStringValue() {
        return this.userkey;
    }

    public String toString() {
        return this.getStringValue();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserKey userKey = (UserKey)o;
        return !(this.userkey != null ? !this.userkey.equals(userKey.userkey) : userKey.userkey != null);
    }

    public int hashCode() {
        return this.userkey != null ? this.userkey.hashCode() : 0;
    }

    public static UserKey fromLong(long userId) {
        return new UserKey(String.valueOf(userId));
    }
}

