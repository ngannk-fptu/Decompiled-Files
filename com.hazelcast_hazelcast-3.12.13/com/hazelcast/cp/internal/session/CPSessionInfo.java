/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.cp.internal.session.RaftSessionServiceDataSerializerHook;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class CPSessionInfo
implements CPSession,
IdentifiedDataSerializable {
    private long id;
    private long version;
    private Address endpoint;
    private String endpointName;
    private CPSession.CPSessionOwnerType endpointType;
    private long creationTime;
    private long expirationTime;

    public CPSessionInfo() {
    }

    CPSessionInfo(long id, long version, Address endpoint, String endpointName, CPSession.CPSessionOwnerType endpointType, long creationTime, long expirationTime) {
        Preconditions.checkTrue(version >= 0L, "Session: " + id + " cannot have a negative version: " + version);
        this.id = id;
        this.version = version;
        this.endpoint = endpoint;
        this.endpointName = endpointName;
        this.endpointType = endpointType;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
    }

    @Override
    public long id() {
        return this.id;
    }

    @Override
    public long creationTime() {
        return this.creationTime;
    }

    @Override
    public long expirationTime() {
        return this.expirationTime;
    }

    @Override
    public boolean isExpired(long timestamp) {
        return this.expirationTime() <= timestamp;
    }

    @Override
    public long version() {
        return this.version;
    }

    @Override
    public Address endpoint() {
        return this.endpoint;
    }

    @Override
    public CPSession.CPSessionOwnerType endpointType() {
        return this.endpointType;
    }

    @Override
    public String endpointName() {
        return this.endpointName;
    }

    CPSessionInfo heartbeat(long ttlMs) {
        long newExpirationTime = Math.max(this.expirationTime, CPSessionInfo.toExpirationTime(Clock.currentTimeMillis(), ttlMs));
        return this.newSession(newExpirationTime);
    }

    CPSessionInfo shiftExpirationTime(long durationMs) {
        long newExpirationTime = CPSessionInfo.toExpirationTime(this.expirationTime, durationMs);
        return this.newSession(newExpirationTime);
    }

    private CPSessionInfo newSession(long newExpirationTime) {
        return new CPSessionInfo(this.id, this.version + 1L, this.endpoint, this.endpointName, this.endpointType, this.creationTime, newExpirationTime);
    }

    static long toExpirationTime(long timestamp, long ttlMillis) {
        long expirationTime = timestamp + ttlMillis;
        return expirationTime > 0L ? expirationTime : Long.MAX_VALUE;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CPSessionInfo that = (CPSessionInfo)o;
        return this.id == that.id && this.version == that.version;
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 31 * result + (int)(this.version ^ this.version >>> 32);
        return result;
    }

    public String toString() {
        return "CPSessionInfo{id=" + this.id + ", version=" + this.version + ", endpoint=" + this.endpoint + ", endpointName='" + this.endpointName + '\'' + ", endpointType=" + (Object)((Object)this.endpointType) + ", creationTime=" + this.creationTime + ", expirationTime=" + this.expirationTime + '}';
    }

    @Override
    public int getFactoryId() {
        return RaftSessionServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeLong(this.creationTime);
        out.writeLong(this.expirationTime);
        out.writeLong(this.version);
        out.writeObject(this.endpoint);
        boolean containsEndpointName = this.endpointName != null;
        out.writeBoolean(containsEndpointName);
        if (containsEndpointName) {
            out.writeUTF(this.endpointName);
        }
        out.writeUTF(this.endpointType.name());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readLong();
        this.creationTime = in.readLong();
        this.expirationTime = in.readLong();
        this.version = in.readLong();
        this.endpoint = (Address)in.readObject();
        boolean containsEndpointName = in.readBoolean();
        if (containsEndpointName) {
            this.endpointName = in.readUTF();
        }
        this.endpointType = CPSession.CPSessionOwnerType.valueOf(in.readUTF());
    }
}

