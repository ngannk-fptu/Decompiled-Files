/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespace;
import java.util.Collection;

public interface PartitionReplicaVersionManager {
    public Collection<ServiceNamespace> getNamespaces(int var1);

    public boolean isPartitionReplicaVersionStale(int var1, ServiceNamespace var2, long[] var3, int var4);

    public long[] getPartitionReplicaVersions(int var1, ServiceNamespace var2);

    public void updatePartitionReplicaVersions(int var1, ServiceNamespace var2, long[] var3, int var4);

    public long[] incrementPartitionReplicaVersions(int var1, ServiceNamespace var2, int var3);

    public ServiceNamespace getServiceNamespace(Operation var1);
}

