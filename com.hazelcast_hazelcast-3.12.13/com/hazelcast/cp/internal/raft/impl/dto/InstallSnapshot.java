/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.dto;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftDataSerializerHook;
import com.hazelcast.cp.internal.raft.impl.log.SnapshotEntry;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class InstallSnapshot
implements IdentifiedDataSerializable {
    private Endpoint leader;
    private int term;
    private SnapshotEntry snapshot;

    public InstallSnapshot() {
    }

    public InstallSnapshot(Endpoint leader, int term, SnapshotEntry snapshot) {
        this.leader = leader;
        this.term = term;
        this.snapshot = snapshot;
    }

    public Endpoint leader() {
        return this.leader;
    }

    public int term() {
        return this.term;
    }

    public SnapshotEntry snapshot() {
        return this.snapshot;
    }

    @Override
    public int getFactoryId() {
        return RaftDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.leader);
        out.writeInt(this.term);
        out.writeObject(this.snapshot);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.leader = (Endpoint)in.readObject();
        this.term = in.readInt();
        this.snapshot = (SnapshotEntry)in.readObject();
    }

    public String toString() {
        return "InstallSnapshot{leader=" + this.leader + ", term=" + this.term + ", snapshot=" + this.snapshot + '}';
    }
}

