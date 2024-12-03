/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.spi.ServiceNamespace;
import java.util.Arrays;

final class PartitionReplicaFragmentVersions {
    private final int partitionId;
    private final ServiceNamespace namespace;
    private final long[] versions = new long[6];
    private boolean dirty;

    PartitionReplicaFragmentVersions(int partitionId, ServiceNamespace namespace) {
        this.partitionId = partitionId;
        this.namespace = namespace;
    }

    long[] incrementAndGet(int backupCount) {
        int i = 0;
        while (i < backupCount) {
            int n = i++;
            this.versions[n] = this.versions[n] + 1L;
        }
        return this.versions;
    }

    long[] get() {
        return this.versions;
    }

    boolean isStale(long[] newVersions, int replicaIndex) {
        int index = replicaIndex - 1;
        long currentVersion = this.versions[index];
        long newVersion = newVersions[index];
        return currentVersion > newVersion;
    }

    boolean update(long[] newVersions, int replicaIndex) {
        int index = replicaIndex - 1;
        long currentVersion = this.versions[index];
        long nextVersion = newVersions[index];
        if (currentVersion < nextVersion) {
            this.setVersions(newVersions, replicaIndex);
            this.dirty = this.dirty || nextVersion - currentVersion > 1L;
        }
        return !this.dirty;
    }

    private void setVersions(long[] newVersions, int fromReplica) {
        int fromIndex = fromReplica - 1;
        int len = newVersions.length - fromIndex;
        System.arraycopy(newVersions, fromIndex, this.versions, fromIndex, len);
    }

    void set(long[] newVersions, int fromReplica) {
        this.setVersions(newVersions, fromReplica);
        this.dirty = false;
    }

    boolean isDirty() {
        return this.dirty;
    }

    void clear() {
        for (int i = 0; i < this.versions.length; ++i) {
            this.versions[i] = 0L;
        }
        this.dirty = false;
    }

    public String toString() {
        return "PartitionReplicaFragmentVersions{partitionId=" + this.partitionId + ", namespace=" + this.namespace + ", versions=" + Arrays.toString(this.versions) + ", dirty=" + this.dirty + '}';
    }
}

