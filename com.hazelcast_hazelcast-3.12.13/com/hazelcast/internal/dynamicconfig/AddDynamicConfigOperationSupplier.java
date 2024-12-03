/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.dynamicconfig.AddDynamicConfigOperation;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Supplier;

public class AddDynamicConfigOperationSupplier
implements Supplier<Operation> {
    private final ClusterService clusterService;
    private final IdentifiedDataSerializable config;

    public AddDynamicConfigOperationSupplier(ClusterService clusterService, IdentifiedDataSerializable config) {
        this.clusterService = clusterService;
        this.config = config;
    }

    @Override
    public Operation get() {
        return new AddDynamicConfigOperation(this.config, this.clusterService.getMemberListVersion());
    }
}

