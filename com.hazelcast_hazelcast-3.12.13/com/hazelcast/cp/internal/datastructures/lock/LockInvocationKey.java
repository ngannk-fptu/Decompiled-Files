/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.cp.internal.datastructures.lock.LockEndpoint;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKey;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.UUID;

public class LockInvocationKey
extends WaitKey
implements IdentifiedDataSerializable {
    private LockEndpoint endpoint;

    public LockInvocationKey() {
    }

    public LockInvocationKey(long commitIndex, UUID invocationUid, Address callerAddress, long callId, LockEndpoint endpoint) {
        super(commitIndex, invocationUid, callerAddress, callId);
        Preconditions.checkNotNull(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    public long sessionId() {
        return this.endpoint.sessionId();
    }

    LockEndpoint endpoint() {
        return this.endpoint;
    }

    boolean isDifferentInvocationOf(LockEndpoint endpoint, UUID invocationUid) {
        return this.endpoint().equals(endpoint) && !this.invocationUid().equals(invocationUid);
    }

    @Override
    public int getFactoryId() {
        return RaftLockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.endpoint);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.endpoint = (LockEndpoint)in.readObject();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LockInvocationKey that = (LockInvocationKey)o;
        if (this.commitIndex != that.commitIndex) {
            return false;
        }
        if (!this.invocationUid.equals(that.invocationUid)) {
            return false;
        }
        if (!this.callerAddress.equals(that.callerAddress)) {
            return false;
        }
        if (this.callId != that.callId) {
            return false;
        }
        return this.endpoint.equals(that.endpoint);
    }

    public int hashCode() {
        int result = (int)(this.commitIndex ^ this.commitIndex >>> 32);
        result = 31 * result + this.invocationUid.hashCode();
        result = 31 * result + this.callerAddress.hashCode();
        result = 31 * result + (int)(this.callId ^ this.callId >>> 32);
        result = 31 * result + this.endpoint.hashCode();
        return result;
    }

    public String toString() {
        return "LockInvocationKey{endpoint=" + this.endpoint + ", commitIndex=" + this.commitIndex + ", invocationUid=" + this.invocationUid + ", callerAddress=" + this.callerAddress + ", callId=" + this.callId + '}';
    }
}

