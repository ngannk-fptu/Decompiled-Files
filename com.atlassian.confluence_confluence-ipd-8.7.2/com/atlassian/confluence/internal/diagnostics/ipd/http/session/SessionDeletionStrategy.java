/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http.session;

import com.atlassian.confluence.internal.diagnostics.ipd.http.session.MutableConfluenceHttpSession;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

class SessionDeletionStrategy {
    private final AtomicBoolean deleteStaleSessionsPermit;
    private long nextCheckTime = System.currentTimeMillis();
    private static final long MAX_SESSION_AGE = 14400000L;
    private static final long MIN_TIME_BETWEEN_CHECKS = 30000L;

    SessionDeletionStrategy() {
        this.deleteStaleSessionsPermit = new AtomicBoolean(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void deleteOldSessions(ConcurrentMap<String, MutableConfluenceHttpSession> sessions) {
        long now = System.currentTimeMillis();
        if (now >= this.nextCheckTime && this.deleteStaleSessionsPermit.compareAndSet(false, true)) {
            try {
                sessions.entrySet().removeIf(next -> this.sessionHasExpired((MutableConfluenceHttpSession)next.getValue()));
            }
            finally {
                this.nextCheckTime = now + 30000L;
                this.deleteStaleSessionsPermit.set(false);
            }
        }
    }

    private boolean sessionHasExpired(MutableConfluenceHttpSession userSession) {
        if (userSession == null) {
            return false;
        }
        long sessionAge = ChronoUnit.MILLIS.between(Instant.now(), userSession.getLastAccessTime());
        return sessionAge > 14400000L;
    }
}

