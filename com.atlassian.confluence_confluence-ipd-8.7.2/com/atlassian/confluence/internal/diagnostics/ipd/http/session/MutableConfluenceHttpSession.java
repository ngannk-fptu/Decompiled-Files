/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http.session;

import com.atlassian.confluence.internal.diagnostics.ipd.http.session.ConfluenceHttpSession;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class MutableConfluenceHttpSession
implements ConfluenceHttpSession {
    private final AtomicReference<String> userName;
    private final AtomicReference<Instant> lastAccessTime;
    private final String sessionId;

    MutableConfluenceHttpSession(String sessionId, String userName) {
        this.sessionId = sessionId;
        this.userName = new AtomicReference<String>(userName);
        this.lastAccessTime = new AtomicReference<Instant>(Instant.now());
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public String getUserName() {
        return this.userName.get();
    }

    @Override
    public Instant getLastAccessTime() {
        return this.lastAccessTime.get();
    }

    void recordInteraction(String userName) {
        this.lastAccessTime.set(Instant.now());
        this.userName.set(userName);
    }
}

