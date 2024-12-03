/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http.session;

import com.atlassian.confluence.internal.diagnostics.ipd.http.session.ConfluenceHttpSession;
import java.time.Instant;

public class SnapshotConfluenceHttpSession
implements ConfluenceHttpSession {
    private final String sessionId;
    private final String userName;
    private final Instant lastAccessTime;

    SnapshotConfluenceHttpSession(ConfluenceHttpSession confluenceHttpSession) {
        this.sessionId = confluenceHttpSession.getId();
        this.userName = confluenceHttpSession.getUserName();
        this.lastAccessTime = confluenceHttpSession.getLastAccessTime();
    }

    public SnapshotConfluenceHttpSession(String sessionId, String userName, Instant lastAccessTime) {
        this.sessionId = sessionId;
        this.userName = userName;
        this.lastAccessTime = lastAccessTime;
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public Instant getLastAccessTime() {
        return this.lastAccessTime;
    }

    public String toString() {
        return this.sessionId + " AS:" + this.sessionId + " lat:" + (this.lastAccessTime == null ? null : this.lastAccessTime.toString());
    }
}

