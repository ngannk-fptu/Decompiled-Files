/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.dto;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class AppendFailureResponse
implements IdentifiedDataSerializable {
    private Endpoint follower;
    private int term;
    private long expectedNextIndex;

    public AppendFailureResponse() {
    }

    public AppendFailureResponse(Endpoint follower, int term, long expectedNextIndex) {
        this.follower = follower;
        this.term = term;
        this.expectedNextIndex = expectedNextIndex;
    }

    public Endpoint follower() {
        return this.follower;
    }

    public int term() {
        return this.term;
    }

    public long expectedNextIndex() {
        return this.expectedNextIndex;
    }

    @Override
    public int getFactoryId() {
        return RaftDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.term);
        out.writeObject(this.follower);
        out.writeLong(this.expectedNextIndex);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.term = in.readInt();
        this.follower = (Endpoint)in.readObject();
        this.expectedNextIndex = in.readLong();
    }

    public String toString() {
        return "AppendFailureResponse{follower=" + this.follower + ", term=" + this.term + ", expectedNextIndex=" + this.expectedNextIndex + '}';
    }
}

