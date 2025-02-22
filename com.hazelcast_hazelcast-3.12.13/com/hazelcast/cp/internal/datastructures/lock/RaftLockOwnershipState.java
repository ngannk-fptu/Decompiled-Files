/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.cp.internal.datastructures.lock.RaftLockDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class RaftLockOwnershipState
implements IdentifiedDataSerializable {
    static final RaftLockOwnershipState NOT_LOCKED = new RaftLockOwnershipState(0L, 0, -1L, -1L);
    private long fence;
    private int lockCount;
    private long sessionId;
    private long threadId;

    public RaftLockOwnershipState() {
    }

    public RaftLockOwnershipState(long fence, int lockCount, long sessionId, long threadId) {
        this.fence = fence;
        this.lockCount = lockCount;
        this.sessionId = sessionId;
        this.threadId = threadId;
    }

    public boolean isLocked() {
        return this.fence != 0L;
    }

    public boolean isLockedBy(long sessionId, long threadId) {
        return this.isLocked() && this.sessionId == sessionId && this.threadId == threadId;
    }

    public long getFence() {
        return this.fence;
    }

    public int getLockCount() {
        return this.lockCount;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    public long getThreadId() {
        return this.threadId;
    }

    @Override
    public int getFactoryId() {
        return RaftLockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.fence);
        out.writeInt(this.lockCount);
        out.writeLong(this.sessionId);
        out.writeLong(this.threadId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.fence = in.readLong();
        this.lockCount = in.readInt();
        this.sessionId = in.readLong();
        this.threadId = in.readLong();
    }

    public String toString() {
        return "RaftLockOwnershipState{fence=" + this.fence + ", lockCount=" + this.lockCount + ", sessionId=" + this.sessionId + ", threadId=" + this.threadId + '}';
    }
}

