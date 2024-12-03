/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.monitor.MemberPartitionState;
import com.hazelcast.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;

public class MemberPartitionStateImpl
implements MemberPartitionState {
    public static final int DEFAULT_PARTITION_COUNT = 271;
    List<Integer> partitions = new ArrayList<Integer>(271);
    boolean memberStateSafe;
    long migrationQueueSize;

    @Override
    public List<Integer> getPartitions() {
        return this.partitions;
    }

    @Override
    public boolean isMemberStateSafe() {
        return this.memberStateSafe;
    }

    public void setMemberStateSafe(boolean memberStateSafe) {
        this.memberStateSafe = memberStateSafe;
    }

    @Override
    public long getMigrationQueueSize() {
        return this.migrationQueueSize;
    }

    public void setMigrationQueueSize(long migrationQueueSize) {
        this.migrationQueueSize = migrationQueueSize;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        JsonArray partitionsArray = new JsonArray();
        for (Integer lsPartition : this.partitions) {
            partitionsArray.add(lsPartition);
        }
        root.add("partitions", partitionsArray);
        root.add("memberStateSafe", this.memberStateSafe);
        root.add("migrationQueueSize", this.migrationQueueSize);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonArray jsonPartitions = JsonUtil.getArray(json, "partitions");
        for (JsonValue jsonPartition : jsonPartitions) {
            this.partitions.add(jsonPartition.asInt());
        }
        this.memberStateSafe = JsonUtil.getBoolean(json, "memberStateSafe");
        this.migrationQueueSize = JsonUtil.getInt(json, "migrationQueueSize");
    }

    public String toString() {
        return "MemberPartitionStateImpl{partitions=" + this.partitions + ", memberStateSafe=" + this.memberStateSafe + ", migrationQueueSize=" + this.migrationQueueSize + '}';
    }
}

