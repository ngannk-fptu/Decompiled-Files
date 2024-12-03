/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.user;

import java.util.Objects;

public class UsernameCacheKey {
    private String username;

    public UsernameCacheKey(String username) {
        this.username = username;
    }

    public String get() {
        return this.username;
    }

    public int hashCode() {
        return Objects.hash(this.username);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UsernameCacheKey)) {
            return false;
        }
        UsernameCacheKey that = (UsernameCacheKey)obj;
        return Objects.equals(this.username, that.username);
    }

    public String toString() {
        return this.username;
    }
}

