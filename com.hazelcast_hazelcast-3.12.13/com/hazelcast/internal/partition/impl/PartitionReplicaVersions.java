/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.partition.impl.PartitionReplicaFragmentVersions;
import com.hazelcast.spi.ServiceNamespace;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class PartitionReplicaVersions {
    private final int partitionId;
    private final Map<ServiceNamespace, PartitionReplicaFragmentVersions> fragmentVersionsMap = new HashMap<ServiceNamespace, PartitionReplicaFragmentVersions>();

    PartitionReplicaVersions(int partitionId) {
        this.partitionId = partitionId;
    }

    long[] incrementAndGet(ServiceNamespace namespace, int backupCount) {
        return this.getFragmentVersions(namespace).incrementAndGet(backupCount);
    }

    long[] get(ServiceNamespace namespace) {
        return this.getFragmentVersions(namespace).get();
    }

    boolean isStale(ServiceNamespace namespace, long[] newVersions, int replicaIndex) {
        return this.getFragmentVersions(namespace).isStale(newVersions, replicaIndex);
    }

    boolean update(ServiceNamespace namespace, long[] newVersions, int replicaIndex) {
        return this.getFragmentVersions(namespace).update(newVersions, replicaIndex);
    }

    void set(ServiceNamespace namespace, long[] newVersions, int fromReplica) {
        this.getFragmentVersions(namespace).set(newVersions, fromReplica);
    }

    boolean isDirty(ServiceNamespace namespace) {
        return this.getFragmentVersions(namespace).isDirty();
    }

    void clear(ServiceNamespace namespace) {
        this.getFragmentVersions(namespace).clear();
    }

    private PartitionReplicaFragmentVersions getFragmentVersions(ServiceNamespace namespace) {
        PartitionReplicaFragmentVersions fragmentVersions = this.fragmentVersionsMap.get(namespace);
        if (fragmentVersions == null) {
            fragmentVersions = new PartitionReplicaFragmentVersions(this.partitionId, namespace);
            this.fragmentVersionsMap.put(namespace, fragmentVersions);
        }
        return fragmentVersions;
    }

    void retainNamespaces(Set<ServiceNamespace> namespaces) {
        this.fragmentVersionsMap.keySet().retainAll(namespaces);
    }

    Collection<ServiceNamespace> getNamespaces() {
        return this.fragmentVersionsMap.keySet();
    }

    public String toString() {
        return "PartitionReplicaVersions{partitionId=" + this.partitionId + ", fragmentVersions=" + this.fragmentVersionsMap + '}';
    }
}

