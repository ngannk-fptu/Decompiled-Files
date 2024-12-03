/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.cp.internal.session.RaftSessionServiceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class SessionResponse
implements IdentifiedDataSerializable {
    private long sessionId;
    private long ttlMillis;
    private long heartbeatMillis;

    SessionResponse() {
    }

    public SessionResponse(long sessionId, long ttlMillis, long heartbeatMillis) {
        this.sessionId = sessionId;
        this.ttlMillis = ttlMillis;
        this.heartbeatMillis = heartbeatMillis;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    public long getTtlMillis() {
        return this.ttlMillis;
    }

    public long getHeartbeatMillis() {
        return this.heartbeatMillis;
    }

    @Override
    public int getFactoryId() {
        return RaftSessionServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.sessionId);
        out.writeLong(this.ttlMillis);
        out.writeLong(this.heartbeatMillis);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.sessionId = in.readLong();
        this.ttlMillis = in.readLong();
        this.heartbeatMillis = in.readLong();
    }

    public String toString() {
        return "SessionResponse{sessionId=" + this.sessionId + ", ttlMillis=" + this.ttlMillis + ", heartbeatMillis=" + this.heartbeatMillis + '}';
    }
}

