/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.confluence.event.events.security.SecurityEvent;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutEvent
extends SecurityEvent {
    private static final Logger log = LoggerFactory.getLogger(LogoutEvent.class);
    private static final long serialVersionUID = 1698251296782594424L;
    private boolean explicitLogout;

    public LogoutEvent(Object src, String username, String sessionId) {
        super(src, username, sessionId);
        log.info("User {} logged out by {}", (Object)username, src);
    }

    public LogoutEvent(Object src, String username, String sessionId, String remoteHost, String remoteIP) {
        super(src, username, sessionId, remoteHost, remoteIP);
        this.explicitLogout = true;
        log.info("User {} logged out by {}, initiated by user", (Object)username, src);
    }

    public boolean getExplicitLogout() {
        return this.explicitLogout;
    }

    @Override
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
        LogoutEvent that = (LogoutEvent)o;
        return this.explicitLogout == that.explicitLogout;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.explicitLogout);
    }
}

