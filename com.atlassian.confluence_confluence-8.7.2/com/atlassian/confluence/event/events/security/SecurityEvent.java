/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Authentication;

public abstract class SecurityEvent
extends ConfluenceEvent
implements Authentication {
    private static final long serialVersionUID = -945445550692022745L;
    String username;
    String sessionId;
    String remoteHost;
    String remoteIP;

    protected SecurityEvent(Object src, String username, String sessionId) {
        super(src);
        this.sessionId = sessionId;
        this.username = username;
    }

    protected SecurityEvent(Object src, String username, String sessionId, String remoteHost, String remoteIP) {
        super(src);
        this.username = username;
        this.sessionId = sessionId;
        this.remoteHost = remoteHost;
        this.remoteIP = remoteIP;
    }

    public String getUsername() {
        return this.username;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getRemoteIP() {
        return this.remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }
}

