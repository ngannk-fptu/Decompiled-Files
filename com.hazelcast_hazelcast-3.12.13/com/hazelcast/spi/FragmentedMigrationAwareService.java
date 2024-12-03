/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.ServiceNamespace;
import java.util.Collection;

public interface FragmentedMigrationAwareService
extends MigrationAwareService {
    public Collection<ServiceNamespace> getAllServiceNamespaces(PartitionReplicationEvent var1);

    public boolean isKnownServiceNamespace(ServiceNamespace var1);

    public Operation prepareReplicationOperation(PartitionReplicationEvent var1, Collection<ServiceNamespace> var2);
}

