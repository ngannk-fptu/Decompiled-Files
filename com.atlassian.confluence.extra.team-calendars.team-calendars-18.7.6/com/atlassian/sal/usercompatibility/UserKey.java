/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.sal.usercompatibility;

import com.google.common.base.Preconditions;
import java.io.Serializable;

public final class UserKey
implements Serializable {
    private final String userkey;

    public UserKey(String userkey) {
        Preconditions.checkNotNull((Object)userkey, (Object)"userkey");
        this.userkey = userkey;
    }

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

