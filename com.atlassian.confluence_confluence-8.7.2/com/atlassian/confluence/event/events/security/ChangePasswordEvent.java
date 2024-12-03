/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChangePasswordEvent {
    private final User user;

    public ChangePasswordEvent(@NonNull User user) {
        this.user = user;
    }

    public @NonNull User getUser() {
        return this.user;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChangePasswordEvent that = (ChangePasswordEvent)o;
        return Objects.equals(this.user, that.user);
    }

    public int hashCode() {
        return Objects.hash(this.user);
    }
}

