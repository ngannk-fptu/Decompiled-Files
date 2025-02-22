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

public class AppendSuccessResponse
implements IdentifiedDataSerializable {
    private Endpoint follower;
    private int term;
    private long lastLogIndex;

    public AppendSuccessResponse() {
    }

    public AppendSuccessResponse(Endpoint follower, int term, long lastLogIndex) {
        this.follower = follower;
        this.term = term;
        this.lastLogIndex = lastLogIndex;
    }

    public Endpoint follower() {
        return this.follower;
    }

    public int term() {
        return this.term;
    }

    public long lastLogIndex() {
        return this.lastLogIndex;
    }

    @Override
    public int getFactoryId() {
        return RaftDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.term);
        out.writeObject(this.follower);
        out.writeLong(this.lastLogIndex);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.term = in.readInt();
        this.follower = (Endpoint)in.readObject();
        this.lastLogIndex = in.readLong();
    }

    public String toString() {
        return "AppendSuccessResponse{follower=" + this.follower + ", term=" + this.term + ", lastLogIndex=" + this.lastLogIndex + '}';
    }
}

