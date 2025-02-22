/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.session.AbstractProxySessionManager;

public abstract class SessionAwareProxy {
    protected final RaftGroupId groupId;
    private final AbstractProxySessionManager sessionManager;

    protected SessionAwareProxy(AbstractProxySessionManager sessionManager, RaftGroupId groupId) {
        this.sessionManager = sessionManager;
        this.groupId = groupId;
    }

    public final RaftGroupId getGroupId() {
        return this.groupId;
    }

    protected final Long getOrCreateUniqueThreadId(RaftGroupId groupId) {
        return this.sessionManager.getOrCreateUniqueThreadId(groupId);
    }

    protected final long acquireSession() {
        return this.sessionManager.acquireSession(this.groupId);
    }

    protected final long acquireSession(int count) {
        return this.sessionManager.acquireSession(this.groupId, count);
    }

    protected final void releaseSession(long sessionId) {
        this.sessionManager.releaseSession(this.groupId, sessionId);
    }

    protected final void releaseSession(long sessionId, int count) {
        this.sessionManager.releaseSession(this.groupId, sessionId, count);
    }

    protected final void invalidateSession(long sessionId) {
        this.sessionManager.invalidateSession(this.groupId, sessionId);
    }

    protected final long getSession() {
        return this.sessionManager.getSession(this.groupId);
    }
}

