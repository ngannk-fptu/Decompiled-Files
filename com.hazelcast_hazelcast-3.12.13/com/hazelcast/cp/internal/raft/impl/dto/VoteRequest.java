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

public class VoteRequest
implements IdentifiedDataSerializable {
    private Endpoint candidate;
    private int term;
    private int lastLogTerm;
    private long lastLogIndex;

    public VoteRequest() {
    }

    public VoteRequest(Endpoint candidate, int term, int lastLogTerm, long lastLogIndex) {
        this.term = term;
        this.candidate = candidate;
        this.lastLogTerm = lastLogTerm;
        this.lastLogIndex = lastLogIndex;
    }

    public Endpoint candidate() {
        return this.candidate;
    }

    public int term() {
        return this.term;
    }

    public int lastLogTerm() {
        return this.lastLogTerm;
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
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.term);
        out.writeObject(this.candidate);
        out.writeInt(this.lastLogTerm);
        out.writeLong(this.lastLogIndex);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.term = in.readInt();
        this.candidate = (Endpoint)in.readObject();
        this.lastLogTerm = in.readInt();
        this.lastLogIndex = in.readLong();
    }

    public String toString() {
        return "VoteRequest{candidate=" + this.candidate + ", term=" + this.term + ", lastLogTerm=" + this.lastLogTerm + ", lastLogIndex=" + this.lastLogIndex + '}';
    }
}

