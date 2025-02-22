/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.blocking;

import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.UUID;

public abstract class WaitKey
implements DataSerializable {
    protected long commitIndex;
    protected UUID invocationUid;
    protected Address callerAddress;
    protected long callId;

    public WaitKey() {
    }

    public WaitKey(long commitIndex, UUID invocationUid, Address callerAddress, long callId) {
        Preconditions.checkNotNull(invocationUid);
        Preconditions.checkNotNull(callerAddress);
        this.commitIndex = commitIndex;
        this.invocationUid = invocationUid;
        this.callerAddress = callerAddress;
        this.callId = callId;
    }

    public abstract long sessionId();

    public final long commitIndex() {
        return this.commitIndex;
    }

    public final UUID invocationUid() {
        return this.invocationUid;
    }

    public final Address callerAddress() {
        return this.callerAddress;
    }

    public final long callId() {
        return this.callId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.commitIndex);
        UUIDSerializationUtil.writeUUID(out, this.invocationUid);
        out.writeObject(this.callerAddress);
        out.writeLong(this.callId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.commitIndex = in.readLong();
        this.invocationUid = UUIDSerializationUtil.readUUID(in);
        this.callerAddress = (Address)in.readObject();
        this.callId = in.readLong();
    }
}

