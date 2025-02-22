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

public class PreVoteRequest
implements IdentifiedDataSerializable {
    private Endpoint candidate;
    private int nextTerm;
    private int lastLogTerm;
    private long lastLogIndex;

    public PreVoteRequest() {
    }

    public PreVoteRequest(Endpoint candidate, int nextTerm, int lastLogTerm, long lastLogIndex) {
        this.nextTerm = nextTerm;
        this.candidate = candidate;
        this.lastLogTerm = lastLogTerm;
        this.lastLogIndex = lastLogIndex;
    }

    public Endpoint candidate() {
        return this.candidate;
    }

    public int nextTerm() {
        return this.nextTerm;
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
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.nextTerm);
        out.writeObject(this.candidate);
        out.writeInt(this.lastLogTerm);
        out.writeLong(this.lastLogIndex);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.nextTerm = in.readInt();
        this.candidate = (Endpoint)in.readObject();
        this.lastLogTerm = in.readInt();
        this.lastLogIndex = in.readLong();
    }

    public String toString() {
        return "PreVoteRequest{candidate=" + this.candidate + ", nextTerm=" + this.nextTerm + ", lastLogTerm=" + this.lastLogTerm + ", lastLogIndex=" + this.lastLogIndex + '}';
    }
}

