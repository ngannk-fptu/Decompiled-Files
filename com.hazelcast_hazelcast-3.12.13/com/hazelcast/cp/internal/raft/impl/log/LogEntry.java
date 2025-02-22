/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.log;

import com.hazelcast.cp.internal.raft.impl.RaftDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class LogEntry
implements IdentifiedDataSerializable {
    private int term;
    private long index;
    private Object operation;

    public LogEntry() {
    }

    public LogEntry(int term, long index, Object operation) {
        this.term = term;
        this.index = index;
        this.operation = operation;
    }

    public long index() {
        return this.index;
    }

    public int term() {
        return this.term;
    }

    public Object operation() {
        return this.operation;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.term);
        out.writeLong(this.index);
        out.writeObject(this.operation);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.term = in.readInt();
        this.index = in.readLong();
        this.operation = in.readObject();
    }

    @Override
    public int getFactoryId() {
        return RaftDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 8;
    }

    public String toString() {
        return "LogEntry{term=" + this.term + ", index=" + this.index + ", operation=" + this.operation + '}';
    }
}

