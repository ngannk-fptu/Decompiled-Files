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

public class VoteResponse
implements IdentifiedDataSerializable {
    private Endpoint voter;
    private int term;
    private boolean granted;

    public VoteResponse() {
    }

    public VoteResponse(Endpoint voter, int term, boolean granted) {
        this.voter = voter;
        this.term = term;
        this.granted = granted;
    }

    public Endpoint voter() {
        return this.voter;
    }

    public int term() {
        return this.term;
    }

    public boolean granted() {
        return this.granted;
    }

    @Override
    public int getFactoryId() {
        return RaftDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.term);
        out.writeBoolean(this.granted);
        out.writeObject(this.voter);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.term = in.readInt();
        this.granted = in.readBoolean();
        this.voter = (Endpoint)in.readObject();
    }

    public String toString() {
        return "VoteResponse{voter=" + this.voter + ", term=" + this.term + ", granted=" + this.granted + '}';
    }
}

