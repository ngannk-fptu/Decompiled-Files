/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.partition.MigrationEndpoint;
import java.util.EventObject;

public class PartitionMigrationEvent
extends EventObject {
    private final MigrationEndpoint migrationEndpoint;
    private final int partitionId;
    private final int currentReplicaIndex;
    private final int newReplicaIndex;

    public PartitionMigrationEvent(MigrationEndpoint migrationEndpoint, int partitionId, int currentReplicaIndex, int newReplicaIndex) {
        super(partitionId);
        this.migrationEndpoint = migrationEndpoint;
        this.partitionId = partitionId;
        this.currentReplicaIndex = currentReplicaIndex;
        this.newReplicaIndex = newReplicaIndex;
    }

    public MigrationEndpoint getMigrationEndpoint() {
        return this.migrationEndpoint;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public int getCurrentReplicaIndex() {
        return this.currentReplicaIndex;
    }

    public int getNewReplicaIndex() {
        return this.newReplicaIndex;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PartitionMigrationEvent that = (PartitionMigrationEvent)o;
        if (this.partitionId != that.partitionId) {
            return false;
        }
        if (this.currentReplicaIndex != that.currentReplicaIndex) {
            return false;
        }
        if (this.newReplicaIndex != that.newReplicaIndex) {
            return false;
        }
        return this.migrationEndpoint == that.migrationEndpoint;
    }

    public int hashCode() {
        int result = this.migrationEndpoint.hashCode();
        result = 31 * result + this.partitionId;
        result = 31 * result + this.currentReplicaIndex;
        result = 31 * result + this.newReplicaIndex;
        return result;
    }

    @Override
    public String toString() {
        return "PartitionMigrationEvent{migrationEndpoint=" + (Object)((Object)this.migrationEndpoint) + ", partitionId=" + this.partitionId + ", currentReplicaIndex=" + this.currentReplicaIndex + ", newReplicaIndex=" + this.newReplicaIndex + '}';
    }
}

