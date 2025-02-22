/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.operations.ClientReAuthOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Supplier;

public class ReAuthenticationOperationSupplier
implements Supplier<Operation> {
    private final String uuid;
    private final long authCorrelationId;

    public ReAuthenticationOperationSupplier(String uuid, long authCorrelationId) {
        this.uuid = uuid;
        this.authCorrelationId = authCorrelationId;
    }

    @Override
    public Operation get() {
        return new ClientReAuthOperation(this.uuid, this.authCorrelationId);
    }
}

