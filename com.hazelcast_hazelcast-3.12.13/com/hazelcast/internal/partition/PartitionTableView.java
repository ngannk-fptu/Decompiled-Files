/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.partition;

import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionReplica;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

public class PartitionTableView {
    private final PartitionReplica[][] replicas;
    private final int version;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public PartitionTableView(PartitionReplica[][] replicas, int version) {
        this.replicas = replicas;
        this.version = version;
    }

    public PartitionTableView(InternalPartition[] partitions, int version) {
        PartitionReplica[][] a = new PartitionReplica[partitions.length][7];
        for (InternalPartition partition : partitions) {
            int partitionId = partition.getPartitionId();
            for (int replica = 0; replica < 7; ++replica) {
                a[partitionId][replica] = partition.getReplica(replica);
            }
        }
        this.replicas = a;
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }

    public PartitionReplica getReplica(int partitionId, int replicaIndex) {
        return this.replicas[partitionId][replicaIndex];
    }

    public int getLength() {
        return this.replicas.length;
    }

    public PartitionReplica[] getReplicas(int partitionId) {
        PartitionReplica[] a = this.replicas[partitionId];
        return Arrays.copyOf(a, a.length);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PartitionTableView that = (PartitionTableView)o;
        return this.version == that.version && Arrays.deepEquals((Object[])this.replicas, (Object[])that.replicas);
    }

    public int hashCode() {
        int result = Arrays.deepHashCode((Object[])this.replicas);
        result = 31 * result + this.version;
        return result;
    }

    public String toString() {
        return "PartitionTable{addresses=" + Arrays.deepToString((Object[])this.replicas) + ", version=" + this.version + '}';
    }
}

