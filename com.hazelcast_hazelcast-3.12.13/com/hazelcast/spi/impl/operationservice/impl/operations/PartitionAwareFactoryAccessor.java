/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl.operations;

import com.hazelcast.client.impl.operations.OperationFactoryWrapper;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;

public final class PartitionAwareFactoryAccessor {
    private PartitionAwareFactoryAccessor() {
    }

    public static PartitionAwareOperationFactory extractPartitionAware(OperationFactory operationFactory) {
        OperationFactory unwrapped;
        if (operationFactory instanceof PartitionAwareOperationFactory) {
            return (PartitionAwareOperationFactory)operationFactory;
        }
        if (operationFactory instanceof OperationFactoryWrapper && (unwrapped = ((OperationFactoryWrapper)operationFactory).getOperationFactory()) instanceof PartitionAwareOperationFactory) {
            return (PartitionAwareOperationFactory)unwrapped;
        }
        return null;
    }
}

