/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore;

import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.semaphore.SemaphoreEndpoint;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKey;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.UUID;

public class AcquireInvocationKey
extends WaitKey
implements IdentifiedDataSerializable {
    private SemaphoreEndpoint endpoint;
    private int permits;

    AcquireInvocationKey() {
    }

    public AcquireInvocationKey(long commitIndex, UUID invocationUid, Address callerAddress, long callId, SemaphoreEndpoint endpoint, int permits) {
        super(commitIndex, invocationUid, callerAddress, callId);
        Preconditions.checkNotNull(endpoint);
        Preconditions.checkTrue(permits > 0, "permits must be positive");
        this.endpoint = endpoint;
        this.permits = permits;
    }

    @Override
    public long sessionId() {
        return this.endpoint.sessionId();
    }

    public SemaphoreEndpoint endpoint() {
        return this.endpoint;
    }

    public int permits() {
        return this.permits;
    }

    boolean isDifferentInvocationOf(SemaphoreEndpoint endpoint, UUID invocationUid) {
        return this.endpoint().equals(endpoint) && !this.invocationUid().equals(invocationUid);
    }

    @Override
    public int getFactoryId() {
        return RaftSemaphoreDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.endpoint);
        out.writeInt(this.permits);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.endpoint = (SemaphoreEndpoint)in.readObject();
        this.permits = in.readInt();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AcquireInvocationKey that = (AcquireInvocationKey)o;
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
        if (!this.endpoint.equals(that.endpoint)) {
            return false;
        }
        return this.permits == that.permits;
    }

    public int hashCode() {
        int result = (int)(this.commitIndex ^ this.commitIndex >>> 32);
        result = 31 * result + this.invocationUid.hashCode();
        result = 31 * result + this.callerAddress.hashCode();
        result = 31 * result + (int)(this.callId ^ this.callId >>> 32);
        result = 31 * result + this.endpoint.hashCode();
        result = 31 * result + this.permits;
        return result;
    }

    public String toString() {
        return "AcquireInvocationKey{endpoint=" + this.endpoint + ", permits=" + this.permits + ", commitIndex=" + this.commitIndex + ", invocationUid=" + this.invocationUid + ", callerAddress=" + this.callerAddress + ", callId=" + this.callId + '}';
    }
}

