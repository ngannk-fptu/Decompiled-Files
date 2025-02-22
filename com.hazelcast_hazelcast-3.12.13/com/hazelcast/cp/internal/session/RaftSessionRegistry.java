/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.session.CPSessionInfo;
import com.hazelcast.cp.internal.session.RaftSessionServiceDataSerializerHook;
import com.hazelcast.cp.internal.session.SessionExpiredException;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Clock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class RaftSessionRegistry
implements IdentifiedDataSerializable {
    private CPGroupId groupId;
    private final Map<Long, CPSessionInfo> sessions = new ConcurrentHashMap<Long, CPSessionInfo>();
    private long nextSessionId;
    private long generatedThreadId;

    RaftSessionRegistry() {
    }

    RaftSessionRegistry(CPGroupId groupId) {
        this.groupId = groupId;
    }

    CPGroupId groupId() {
        return this.groupId;
    }

    CPSessionInfo getSession(long sessionId) {
        return this.sessions.get(sessionId);
    }

    long createNewSession(long sessionTTLMs, Address endpoint, String endpointName, CPSession.CPSessionOwnerType endpointType, long creationTime) {
        long id = ++this.nextSessionId;
        long expirationTime = CPSessionInfo.toExpirationTime(creationTime, sessionTTLMs);
        CPSessionInfo session = new CPSessionInfo(id, 0L, endpoint, endpointName, endpointType, creationTime, expirationTime);
        this.sessions.put(id, session);
        return id;
    }

    boolean closeSession(long sessionId) {
        return this.sessions.remove(sessionId) != null;
    }

    boolean expireSession(long sessionId, long expectedVersion) {
        CPSessionInfo session = this.sessions.get(sessionId);
        if (session == null) {
            return false;
        }
        if (session.version() != expectedVersion) {
            return false;
        }
        this.sessions.remove(sessionId);
        return true;
    }

    void heartbeat(long sessionId, long sessionTTLMs) {
        CPSessionInfo session = this.getSessionOrFail(sessionId);
        this.sessions.put(sessionId, session.heartbeat(sessionTTLMs));
    }

    void shiftExpirationTimes(long durationMs) {
        for (CPSessionInfo session : this.sessions.values()) {
            this.sessions.put(session.id(), session.shiftExpirationTime(durationMs));
        }
    }

    Collection<Tuple2<Long, Long>> getSessionsToExpire() {
        ArrayList<Tuple2<Long, Long>> expired = new ArrayList<Tuple2<Long, Long>>();
        long now = Clock.currentTimeMillis();
        for (CPSessionInfo session : this.sessions.values()) {
            if (!session.isExpired(now)) continue;
            expired.add(Tuple2.of(session.id(), session.version()));
        }
        return expired;
    }

    private CPSessionInfo getSessionOrFail(long sessionId) {
        CPSessionInfo session = this.sessions.get(sessionId);
        if (session == null) {
            throw new SessionExpiredException();
        }
        return session;
    }

    Collection<? extends CPSession> getSessions() {
        return this.sessions.values();
    }

    RaftSessionRegistry cloneForSnapshot() {
        RaftSessionRegistry clone = new RaftSessionRegistry();
        clone.groupId = this.groupId;
        clone.sessions.putAll(this.sessions);
        clone.nextSessionId = this.nextSessionId;
        clone.generatedThreadId = this.generatedThreadId;
        return clone;
    }

    long generateThreadId() {
        return ++this.generatedThreadId;
    }

    @Override
    public int getFactoryId() {
        return RaftSessionServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.groupId);
        out.writeLong(this.nextSessionId);
        out.writeInt(this.sessions.size());
        for (CPSessionInfo session : this.sessions.values()) {
            out.writeObject(session);
        }
        out.writeLong(this.generatedThreadId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.groupId = (CPGroupId)in.readObject();
        this.nextSessionId = in.readLong();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            CPSessionInfo session = (CPSessionInfo)in.readObject();
            this.sessions.put(session.id(), session);
        }
        this.generatedThreadId = in.readLong();
    }
}

