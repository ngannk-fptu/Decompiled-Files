/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.cp.internal.raft.impl.dto;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.RaftDataSerializerHook;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;

public class AppendRequest
implements IdentifiedDataSerializable {
    private Endpoint leader;
    private int term;
    private int prevLogTerm;
    private long prevLogIndex;
    private long leaderCommitIndex;
    private LogEntry[] entries;

    public AppendRequest() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public AppendRequest(Endpoint leader, int term, int prevLogTerm, long prevLogIndex, long leaderCommitIndex, LogEntry[] entries) {
        this.term = term;
        this.leader = leader;
        this.prevLogTerm = prevLogTerm;
        this.prevLogIndex = prevLogIndex;
        this.leaderCommitIndex = leaderCommitIndex;
        this.entries = entries;
    }

    public Endpoint leader() {
        return this.leader;
    }

    public int term() {
        return this.term;
    }

    public int prevLogTerm() {
        return this.prevLogTerm;
    }

    public long prevLogIndex() {
        return this.prevLogIndex;
    }

    public long leaderCommitIndex() {
        return this.leaderCommitIndex;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public LogEntry[] entries() {
        return this.entries;
    }

    public int entryCount() {
        return this.entries.length;
    }

    @Override
    public int getFactoryId() {
        return RaftDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.term);
        out.writeObject(this.leader);
        out.writeInt(this.prevLogTerm);
        out.writeLong(this.prevLogIndex);
        out.writeLong(this.leaderCommitIndex);
        out.writeInt(this.entries.length);
        for (LogEntry entry : this.entries) {
            out.writeObject(entry);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.term = in.readInt();
        this.leader = (Endpoint)in.readObject();
        this.prevLogTerm = in.readInt();
        this.prevLogIndex = in.readLong();
        this.leaderCommitIndex = in.readLong();
        int len = in.readInt();
        this.entries = new LogEntry[len];
        for (int i = 0; i < len; ++i) {
            this.entries[i] = (LogEntry)in.readObject();
        }
    }

    public String toString() {
        return "AppendRequest{leader=" + this.leader + ", term=" + this.term + ", prevLogTerm=" + this.prevLogTerm + ", prevLogIndex=" + this.prevLogIndex + ", leaderCommitIndex=" + this.leaderCommitIndex + ", entries=" + Arrays.toString(this.entries) + '}';
    }
}

