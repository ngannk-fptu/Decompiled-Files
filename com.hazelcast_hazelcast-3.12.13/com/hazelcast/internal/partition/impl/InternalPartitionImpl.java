/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.PartitionListener;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.internal.partition.impl.PartitionReplicaChangeEvent;
import com.hazelcast.nio.Address;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

public class InternalPartitionImpl
implements InternalPartition {
    @SuppressFBWarnings(value={"VO_VOLATILE_REFERENCE_TO_ARRAY"}, justification="The contents of this array will never be updated, so it can be safely read using a volatile read. Writing to `replicas` is done under InternalPartitionServiceImpl.lock, so there's no need to guard `replicas` field or to use a CAS.")
    private volatile PartitionReplica[] replicas = new PartitionReplica[7];
    private final int partitionId;
    private final PartitionListener partitionListener;
    private volatile PartitionReplica localReplica;
    private volatile boolean isMigrating;

    InternalPartitionImpl(int partitionId, PartitionListener partitionListener, PartitionReplica localReplica) {
        assert (localReplica != null);
        this.partitionId = partitionId;
        this.partitionListener = partitionListener;
        this.localReplica = localReplica;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public InternalPartitionImpl(int partitionId, PartitionListener listener, PartitionReplica localReplica, PartitionReplica[] replicas) {
        this(partitionId, listener, localReplica);
        this.replicas = replicas;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public boolean isMigrating() {
        return this.isMigrating;
    }

    public boolean setMigrating() {
        if (this.isMigrating) {
            return false;
        }
        this.isMigrating = true;
        return true;
    }

    public void resetMigrating() {
        this.isMigrating = false;
    }

    @Override
    public boolean isLocal() {
        return this.localReplica.equals(this.getOwnerReplicaOrNull());
    }

    @Override
    public Address getOwnerOrNull() {
        return InternalPartitionImpl.getAddress(this.replicas[0]);
    }

    @Override
    public PartitionReplica getOwnerReplicaOrNull() {
        return this.replicas[0];
    }

    @Override
    public Address getReplicaAddress(int replicaIndex) {
        PartitionReplica member = this.replicas[replicaIndex];
        return InternalPartitionImpl.getAddress(member);
    }

    @Override
    public PartitionReplica getReplica(int replicaIndex) {
        return this.replicas[replicaIndex];
    }

    void swapReplicas(int index1, int index2) {
        PartitionReplica a2;
        PartitionReplica[] newReplicas = Arrays.copyOf(this.replicas, 7);
        PartitionReplica a1 = newReplicas[index1];
        newReplicas[index1] = a2 = newReplicas[index2];
        newReplicas[index2] = a1;
        this.replicas = newReplicas;
        this.callPartitionListener(index1, a1, a2);
        this.callPartitionListener(index2, a2, a1);
    }

    void setInitialReplicas(PartitionReplica[] newReplicas) {
        PartitionReplica[] oldReplicas = this.replicas;
        for (int replicaIndex = 0; replicaIndex < 7; ++replicaIndex) {
            if (oldReplicas[replicaIndex] == null) continue;
            throw new IllegalStateException("Partition is already initialized!");
        }
        this.replicas = newReplicas;
    }

    void setReplicas(PartitionReplica[] newReplicas) {
        PartitionReplica[] oldReplicas = this.replicas;
        this.replicas = newReplicas;
        this.callPartitionListener(newReplicas, oldReplicas);
    }

    void setReplica(int replicaIndex, PartitionReplica newReplica) {
        PartitionReplica[] newReplicas = Arrays.copyOf(this.replicas, 7);
        PartitionReplica oldReplica = newReplicas[replicaIndex];
        newReplicas[replicaIndex] = newReplica;
        this.replicas = newReplicas;
        this.callPartitionListener(replicaIndex, oldReplica, newReplica);
    }

    private void callPartitionListener(PartitionReplica[] newReplicas, PartitionReplica[] oldReplicas) {
        if (this.partitionListener != null) {
            for (int replicaIndex = 0; replicaIndex < 7; ++replicaIndex) {
                PartitionReplica oldReplicasId = oldReplicas[replicaIndex];
                PartitionReplica newReplicasId = newReplicas[replicaIndex];
                this.callPartitionListener(replicaIndex, oldReplicasId, newReplicasId);
            }
        }
    }

    private void callPartitionListener(int replicaIndex, PartitionReplica oldReplica, PartitionReplica newReplica) {
        boolean changed;
        if (oldReplica == null) {
            changed = newReplica != null;
        } else {
            boolean bl = changed = !oldReplica.equals(newReplica);
        }
        if (changed) {
            PartitionReplicaChangeEvent event = new PartitionReplicaChangeEvent(this.partitionId, replicaIndex, InternalPartitionImpl.getAddress(oldReplica), InternalPartitionImpl.getAddress(newReplica));
            this.partitionListener.replicaChanged(event);
        }
    }

    private static Address getAddress(PartitionReplica replica) {
        return replica != null ? replica.address() : null;
    }

    InternalPartitionImpl copy(PartitionListener listener) {
        return new InternalPartitionImpl(this.partitionId, listener, this.localReplica, Arrays.copyOf(this.replicas, 7));
    }

    PartitionReplica[] getReplicas() {
        return this.replicas;
    }

    @Override
    public boolean isOwnerOrBackup(Address address) {
        if (address == null) {
            return false;
        }
        for (int i = 0; i < 7; ++i) {
            if (!address.equals(InternalPartitionImpl.getAddress(this.replicas[i]))) continue;
            return true;
        }
        return false;
    }

    @Override
    public int getReplicaIndex(PartitionReplica replica) {
        return InternalPartitionImpl.getReplicaIndex(this.replicas, replica);
    }

    public boolean isOwnerOrBackup(PartitionReplica replica) {
        return InternalPartitionImpl.getReplicaIndex(this.replicas, replica) >= 0;
    }

    static int getReplicaIndex(PartitionReplica[] replicas, PartitionReplica replica) {
        if (replica == null) {
            return -1;
        }
        for (int i = 0; i < 7; ++i) {
            if (!replica.equals(replicas[i])) continue;
            return i;
        }
        return -1;
    }

    int replaceReplica(PartitionReplica oldReplica, PartitionReplica newReplica) {
        PartitionReplica currentReplica;
        for (int i = 0; i < 7 && (currentReplica = this.replicas[i]) != null; ++i) {
            if (!currentReplica.equals(oldReplica)) continue;
            PartitionReplica[] newReplicas = Arrays.copyOf(this.replicas, 7);
            newReplicas[i] = newReplica;
            this.replicas = newReplicas;
            this.callPartitionListener(i, oldReplica, newReplica);
            return i;
        }
        return -1;
    }

    void reset(PartitionReplica localReplica) {
        assert (localReplica != null);
        this.replicas = new PartitionReplica[7];
        this.localReplica = localReplica;
        this.resetMigrating();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Partition [").append(this.partitionId).append("]{\n");
        for (int i = 0; i < 7; ++i) {
            PartitionReplica replica = this.replicas[i];
            if (replica == null) continue;
            sb.append('\t');
            sb.append(i).append(":").append(replica);
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}

