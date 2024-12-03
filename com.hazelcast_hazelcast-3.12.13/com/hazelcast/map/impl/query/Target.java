/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class Target
implements IdentifiedDataSerializable {
    public static final Target ALL_NODES = new Target(TargetMode.ALL_NODES, null);
    public static final Target LOCAL_NODE = new Target(TargetMode.LOCAL_NODE, null);
    private TargetMode mode;
    private Integer partitionId;

    public Target() {
    }

    private Target(TargetMode mode, Integer partitionId) {
        this.mode = Preconditions.checkNotNull(mode);
        this.partitionId = partitionId;
        if (mode.equals((Object)TargetMode.PARTITION_OWNER) && partitionId == null) {
            throw new IllegalArgumentException("It's forbidden to use null partitionId with PARTITION_OWNER mode");
        }
    }

    public TargetMode mode() {
        return this.mode;
    }

    public Integer partitionId() {
        return this.partitionId;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 117;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.partitionId);
        out.writeUTF(this.mode.name());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.partitionId = in.readInt();
        this.mode = TargetMode.valueOf(in.readUTF());
    }

    public static Target createPartitionTarget(int partitionId) {
        return new Target(TargetMode.PARTITION_OWNER, partitionId);
    }

    static enum TargetMode {
        LOCAL_NODE,
        ALL_NODES,
        PARTITION_OWNER;

    }
}

