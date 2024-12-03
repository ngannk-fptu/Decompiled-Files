/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.confluence.event.events.types.Authentication;
import com.atlassian.event.Event;
import com.atlassian.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RpcAuthenticatedEvent
extends Event
implements Authentication {
    private static final long serialVersionUID = 2839600387118638075L;
    private User user;
    private String token;

    public RpcAuthenticatedEvent(Object source, User user, String token) {
        super(source);
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return this.user;
    }

    public String getToken() {
        return this.token;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RpcAuthenticatedEvent that = (RpcAuthenticatedEvent)o;
        return Objects.equals(this.user, that.user) && Objects.equals(this.token, that.token);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.user, this.token);
    }
}

