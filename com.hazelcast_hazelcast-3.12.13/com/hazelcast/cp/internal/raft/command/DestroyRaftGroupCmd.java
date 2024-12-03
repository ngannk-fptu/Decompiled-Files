/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.command;

import com.hazelcast.cp.internal.raft.command.RaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.RaftDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class DestroyRaftGroupCmd
extends RaftGroupCmd
implements IdentifiedDataSerializable {
    @Override
    public int getFactoryId() {
        return RaftDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    public void writeData(ObjectDataOutput out) {
    }

    @Override
    public void readData(ObjectDataInput in) {
    }

    public String toString() {
        return "DestroyRaftGroupCmd{}";
    }
}

