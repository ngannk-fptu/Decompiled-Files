/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl.operations;

import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.eventservice.impl.Registration;
import com.hazelcast.spi.impl.eventservice.impl.operations.RegistrationOperation;
import com.hazelcast.util.function.Supplier;

public class RegistrationOperationSupplier
implements Supplier<Operation> {
    private final Registration reg;
    private final ClusterService clusterService;

    public RegistrationOperationSupplier(Registration reg, ClusterService clusterService) {
        this.reg = reg;
        this.clusterService = clusterService;
    }

    @Override
    public Operation get() {
        return new RegistrationOperation(this.reg, this.clusterService.getMemberListVersion());
    }
}

