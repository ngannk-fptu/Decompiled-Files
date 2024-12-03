/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.confluence.event.events.types.Authentication;
import com.atlassian.event.Event;
import java.util.Objects;

public class RpcAuthFailedEvent
extends Event
implements Authentication {
    private static final long serialVersionUID = -3868932237842316940L;
    private final String username;

    public RpcAuthFailedEvent(Object source, String username) {
        super(source);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RpcAuthFailedEvent that = (RpcAuthFailedEvent)o;
        return Objects.equals(this.username, that.username);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.username);
    }
}

