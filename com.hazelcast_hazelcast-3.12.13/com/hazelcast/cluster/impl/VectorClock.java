/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cluster.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VectorClock
implements IdentifiedDataSerializable {
    private final Map<String, Long> replicaTimestamps = new ConcurrentHashMap<String, Long>();

    public VectorClock() {
    }

    public VectorClock(VectorClock from) {
        this.replicaTimestamps.putAll(from.replicaTimestamps);
    }

    public Long getTimestampForReplica(String replicaId) {
        return this.replicaTimestamps.get(replicaId);
    }

    public void setReplicaTimestamp(String replicaId, long timestamp) {
        this.replicaTimestamps.put(replicaId, timestamp);
    }

    public void merge(VectorClock other) {
        for (Map.Entry<String, Long> entry : other.replicaTimestamps.entrySet()) {
            String replicaId = entry.getKey();
            long mergingTimestamp = entry.getValue();
            long localTimestamp = this.replicaTimestamps.containsKey(replicaId) ? this.replicaTimestamps.get(replicaId) : Long.MIN_VALUE;
            this.replicaTimestamps.put(replicaId, Math.max(localTimestamp, mergingTimestamp));
        }
    }

    public boolean isAfter(VectorClock other) {
        boolean anyTimestampGreater = false;
        for (Map.Entry<String, Long> otherEntry : other.replicaTimestamps.entrySet()) {
            String replicaId = otherEntry.getKey();
            Long otherReplicaTimestamp = otherEntry.getValue();
            Long localReplicaTimestamp = this.getTimestampForReplica(replicaId);
            if (localReplicaTimestamp == null || localReplicaTimestamp < otherReplicaTimestamp) {
                return false;
            }
            if (localReplicaTimestamp <= otherReplicaTimestamp) continue;
            anyTimestampGreater = true;
        }
        return anyTimestampGreater || other.replicaTimestamps.size() < this.replicaTimestamps.size();
    }

    public boolean isEmpty() {
        return this.replicaTimestamps.isEmpty();
    }

    public Set<Map.Entry<String, Long>> entrySet() {
        return this.replicaTimestamps.entrySet();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.replicaTimestamps.size());
        for (Map.Entry<String, Long> timestampEntry : this.replicaTimestamps.entrySet()) {
            String replicaId = timestampEntry.getKey();
            Long timestamp = timestampEntry.getValue();
            out.writeUTF(replicaId);
            out.writeLong(timestamp);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int stateSize = in.readInt();
        for (int i = 0; i < stateSize; ++i) {
            String replicaId = in.readUTF();
            long timestamp = in.readLong();
            this.replicaTimestamps.put(replicaId, timestamp);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VectorClock that = (VectorClock)o;
        return this.replicaTimestamps.equals(that.replicaTimestamps);
    }

    public int hashCode() {
        return this.replicaTimestamps.hashCode();
    }

    public String toString() {
        return this.replicaTimestamps.toString();
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 43;
    }
}

