/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.cp.internal.datastructures.lock.RaftLockDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class LockEndpoint
implements IdentifiedDataSerializable {
    private long sessionId;
    private long threadId;

    public LockEndpoint() {
    }

    public LockEndpoint(long sessionId, long threadId) {
        Preconditions.checkTrue(sessionId != -1L, "a session id must be provided");
        this.sessionId = sessionId;
        this.threadId = threadId;
    }

    public long sessionId() {
        return this.sessionId;
    }

    public long threadId() {
        return this.threadId;
    }

    @Override
    public int getFactoryId() {
        return RaftLockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.sessionId);
        out.writeLong(this.threadId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.sessionId = in.readLong();
        this.threadId = in.readLong();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LockEndpoint)) {
            return false;
        }
        LockEndpoint that = (LockEndpoint)o;
        if (this.sessionId != that.sessionId) {
            return false;
        }
        return this.threadId == that.threadId;
    }

    public int hashCode() {
        int result = (int)(this.sessionId ^ this.sessionId >>> 32);
        result = 31 * result + (int)(this.threadId ^ this.threadId >>> 32);
        return result;
    }

    public String toString() {
        return "LockEndpoint{sessionId=" + this.sessionId + ", threadId=" + this.threadId + '}';
    }
}

