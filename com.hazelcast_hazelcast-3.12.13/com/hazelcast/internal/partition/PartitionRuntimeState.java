/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.impl.PartitionDataSerializerHook;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.util.StringUtil;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PartitionRuntimeState
implements IdentifiedDataSerializable,
Versioned {
    private PartitionReplica[] replicas;
    private int[][] minimizedPartitionTable;
    private int version;
    private Collection<MigrationInfo> completedMigrations;
    private MigrationInfo activeMigration;
    private Address master;

    public PartitionRuntimeState() {
    }

    public PartitionRuntimeState(InternalPartition[] partitions, Collection<MigrationInfo> completedMigrations, int version) {
        this.version = version;
        this.completedMigrations = completedMigrations != null ? completedMigrations : Collections.emptyList();
        Map<PartitionReplica, Integer> replicaToIndexes = this.createPartitionReplicaToIndexMap(partitions);
        this.replicas = this.toPartitionReplicaArray(replicaToIndexes);
        this.minimizedPartitionTable = this.createMinimizedPartitionTable(partitions, replicaToIndexes);
    }

    private PartitionReplica[] toPartitionReplicaArray(Map<PartitionReplica, Integer> addressToIndexes) {
        PartitionReplica[] replicas = new PartitionReplica[addressToIndexes.size()];
        for (Map.Entry<PartitionReplica, Integer> entry : addressToIndexes.entrySet()) {
            replicas[entry.getValue().intValue()] = entry.getKey();
        }
        return replicas;
    }

    private int[][] createMinimizedPartitionTable(InternalPartition[] partitions, Map<PartitionReplica, Integer> replicaToIndexes) {
        int[][] partitionTable = new int[partitions.length][7];
        for (InternalPartition partition : partitions) {
            int[] indexes = partitionTable[partition.getPartitionId()];
            for (int replicaIndex = 0; replicaIndex < 7; ++replicaIndex) {
                int index;
                PartitionReplica replica = partition.getReplica(replicaIndex);
                indexes[replicaIndex] = replica == null ? -1 : (index = replicaToIndexes.get(replica).intValue());
            }
        }
        return partitionTable;
    }

    private Map<PartitionReplica, Integer> createPartitionReplicaToIndexMap(InternalPartition[] partitions) {
        HashMap<PartitionReplica, Integer> map = new HashMap<PartitionReplica, Integer>();
        int addressIndex = 0;
        for (InternalPartition partition : partitions) {
            for (int i = 0; i < 7; ++i) {
                PartitionReplica replica = partition.getReplica(i);
                if (replica == null || map.containsKey(replica)) continue;
                map.put(replica, addressIndex++);
            }
        }
        return map;
    }

    public PartitionReplica[][] getPartitionTable() {
        int length = this.minimizedPartitionTable.length;
        PartitionReplica[][] result = new PartitionReplica[length][7];
        for (int partitionId = 0; partitionId < length; ++partitionId) {
            int[] addressIndexes = this.minimizedPartitionTable[partitionId];
            for (int replicaIndex = 0; replicaIndex < addressIndexes.length; ++replicaIndex) {
                int index = addressIndexes[replicaIndex];
                if (index == -1) continue;
                PartitionReplica replica = this.replicas[index];
                assert (replica != null);
                result[partitionId][replicaIndex] = replica;
            }
        }
        return result;
    }

    public Address getMaster() {
        return this.master;
    }

    public void setMaster(Address master) {
        this.master = master;
    }

    public Collection<MigrationInfo> getCompletedMigrations() {
        return this.completedMigrations != null ? this.completedMigrations : Collections.emptyList();
    }

    public MigrationInfo getActiveMigration() {
        return this.activeMigration;
    }

    public void setActiveMigration(MigrationInfo activeMigration) {
        this.activeMigration = activeMigration;
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int k;
        this.version = in.readInt();
        int memberCount = in.readInt();
        this.replicas = new PartitionReplica[memberCount];
        Version version = in.getVersion();
        for (int i = 0; i < memberCount; ++i) {
            PartitionReplica replica;
            if (version.isGreaterOrEqual(Versions.V3_12)) {
                replica = (PartitionReplica)in.readObject();
            } else {
                Address address = new Address();
                address.readData(in);
                replica = new PartitionReplica(address, "<unknown-uuid>");
            }
            int index = in.readInt();
            assert (this.replicas[index] == null) : "Duplicate replica! Member: " + replica + ", index: " + index + ", addresses: " + Arrays.toString(this.replicas);
            this.replicas[index] = replica;
        }
        int partitionCount = in.readInt();
        this.minimizedPartitionTable = new int[partitionCount][7];
        for (int i = 0; i < partitionCount; ++i) {
            int[] indexes = this.minimizedPartitionTable[i];
            for (int ix = 0; ix < 7; ++ix) {
                indexes[ix] = in.readInt();
            }
        }
        if (in.readBoolean()) {
            if (version.isGreaterOrEqual(Versions.V3_12)) {
                this.activeMigration = (MigrationInfo)in.readObject();
            } else {
                this.activeMigration = new MigrationInfo();
                this.activeMigration.readData(in);
            }
        }
        if ((k = in.readInt()) > 0) {
            this.completedMigrations = new ArrayList<MigrationInfo>(k);
            for (int i = 0; i < k; ++i) {
                MigrationInfo migrationInfo;
                if (version.isGreaterOrEqual(Versions.V3_12)) {
                    migrationInfo = (MigrationInfo)in.readObject();
                } else {
                    migrationInfo = new MigrationInfo();
                    migrationInfo.readData(in);
                }
                this.completedMigrations.add(migrationInfo);
            }
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.version);
        Version version = out.getVersion();
        out.writeInt(this.replicas.length);
        for (int index = 0; index < this.replicas.length; ++index) {
            PartitionReplica replica = this.replicas[index];
            if (version.isGreaterOrEqual(Versions.V3_12)) {
                out.writeObject(replica);
            } else {
                replica.address().writeData(out);
            }
            out.writeInt(index);
        }
        out.writeInt(this.minimizedPartitionTable.length);
        for (int[] indexes : this.minimizedPartitionTable) {
            for (int ix = 0; ix < 7; ++ix) {
                out.writeInt(indexes[ix]);
            }
        }
        if (this.activeMigration != null) {
            out.writeBoolean(true);
            if (version.isGreaterOrEqual(Versions.V3_12)) {
                out.writeObject(this.activeMigration);
            } else {
                this.activeMigration.writeData(out);
            }
        } else {
            out.writeBoolean(false);
        }
        if (this.completedMigrations != null) {
            int k = this.completedMigrations.size();
            out.writeInt(k);
            for (MigrationInfo migrationInfo : this.completedMigrations) {
                if (version.isGreaterOrEqual(Versions.V3_12)) {
                    out.writeObject(migrationInfo);
                    continue;
                }
                migrationInfo.writeData(out);
            }
        } else {
            out.writeInt(0);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PartitionRuntimeState [" + this.version + "]{" + StringUtil.LINE_SEPARATOR);
        for (PartitionReplica replica : this.replicas) {
            sb.append(replica).append(StringUtil.LINE_SEPARATOR);
        }
        sb.append(", completedMigrations=").append(this.completedMigrations);
        sb.append('}');
        return sb.toString();
    }

    public int getVersion() {
        return this.version;
    }

    @Override
    public int getFactoryId() {
        return PartitionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }
}

