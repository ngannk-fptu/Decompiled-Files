/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.log;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class SnapshotEntry
extends LogEntry
implements IdentifiedDataSerializable {
    private long groupMembersLogIndex;
    private Collection<Endpoint> groupMembers;

    public SnapshotEntry() {
    }

    public SnapshotEntry(int term, long index, Object operation, long groupMembersLogIndex, Collection<Endpoint> groupMembers) {
        super(term, index, operation);
        this.groupMembersLogIndex = groupMembersLogIndex;
        this.groupMembers = groupMembers;
    }

    public long groupMembersLogIndex() {
        return this.groupMembersLogIndex;
    }

    public Collection<Endpoint> groupMembers() {
        return this.groupMembers;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeLong(this.groupMembersLogIndex);
        out.writeInt(this.groupMembers.size());
        for (Endpoint endpoint : this.groupMembers) {
            out.writeObject(endpoint);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.groupMembersLogIndex = in.readLong();
        int count = in.readInt();
        this.groupMembers = new HashSet<Endpoint>(count);
        for (int i = 0; i < count; ++i) {
            Endpoint endpoint = (Endpoint)in.readObject();
            this.groupMembers.add(endpoint);
        }
    }

    @Override
    public int getId() {
        return 9;
    }

    public String toString(boolean detailed) {
        return "SnapshotEntry{term=" + this.term() + ", index=" + this.index() + (detailed ? ", operation=" + this.operation() : "") + ", groupMembersLogIndex=" + this.groupMembersLogIndex + ", groupMembers=" + this.groupMembers + '}';
    }

    @Override
    public String toString() {
        return this.toString(false);
    }
}

