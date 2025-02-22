/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch;

import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKey;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.UUID;

public class AwaitInvocationKey
extends WaitKey
implements IdentifiedDataSerializable {
    AwaitInvocationKey() {
    }

    public AwaitInvocationKey(long commitIndex, UUID invocationUid, Address callerAddress, long callId) {
        super(commitIndex, invocationUid, callerAddress, callId);
    }

    @Override
    public long sessionId() {
        return -1L;
    }

    @Override
    public int getFactoryId() {
        return RaftCountDownLatchDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwaitInvocationKey that = (AwaitInvocationKey)o;
        if (this.commitIndex != that.commitIndex) {
            return false;
        }
        if (!this.invocationUid.equals(that.invocationUid)) {
            return false;
        }
        if (!this.callerAddress.equals(that.callerAddress)) {
            return false;
        }
        return this.callId == that.callId;
    }

    public int hashCode() {
        int result = (int)(this.commitIndex ^ this.commitIndex >>> 32);
        result = 31 * result + this.invocationUid.hashCode();
        result = 31 * result + this.callerAddress.hashCode();
        result = 31 * result + (int)(this.callId ^ this.callId >>> 32);
        return result;
    }

    public String toString() {
        return "AwaitInvocationKey{commitIndex=" + this.commitIndex + ", invocationUid=" + this.invocationUid + ", callerAddress=" + this.callerAddress + ", callId=" + this.callId + '}';
    }
}

