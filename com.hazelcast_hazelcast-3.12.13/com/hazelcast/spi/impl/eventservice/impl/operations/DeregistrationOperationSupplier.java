/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl.operations;

import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.eventservice.impl.Registration;
import com.hazelcast.spi.impl.eventservice.impl.operations.DeregistrationOperation;
import com.hazelcast.util.function.Supplier;

public class DeregistrationOperationSupplier
implements Supplier<Operation> {
    private final Registration registration;
    private final ClusterService clusterService;

    public DeregistrationOperationSupplier(Registration reg, ClusterService clusterService) {
        this.registration = reg;
        this.clusterService = clusterService;
    }

    @Override
    public Operation get() {
        return new DeregistrationOperation(this.registration.getTopic(), this.registration.getId(), this.clusterService.getMemberListVersion()).setServiceName(this.registration.getServiceName());
    }
}

