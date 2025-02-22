/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

import com.hazelcast.map.impl.querycache.InvokerWrapper;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.Preconditions;
import java.util.Map;
import java.util.concurrent.Future;

public class NodeInvokerWrapper
implements InvokerWrapper {
    private final OperationService operationService;

    NodeInvokerWrapper(OperationService operationService) {
        this.operationService = operationService;
    }

    @Override
    public Future invokeOnPartitionOwner(Object operation, int partitionId) {
        Preconditions.checkNotNull(operation, "operation cannot be null");
        Preconditions.checkNotNegative(partitionId, "partitionId");
        Operation op = (Operation)operation;
        return this.operationService.invokeOnPartition("hz:impl:mapService", op, partitionId);
    }

    @Override
    public Map<Integer, Object> invokeOnAllPartitions(Object request) throws Exception {
        Preconditions.checkInstanceOf(OperationFactory.class, request, "request");
        OperationFactory factory = (OperationFactory)request;
        return this.operationService.invokeOnAllPartitions("hz:impl:mapService", factory);
    }

    @Override
    public Future invokeOnTarget(Object operation, Address address) {
        Preconditions.checkNotNull(operation, "operation cannot be null");
        Preconditions.checkNotNull(address, "address cannot be null");
        Operation op = (Operation)operation;
        return this.operationService.invokeOnTarget("hz:impl:mapService", op, address);
    }

    @Override
    public Object invoke(Object operation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void executeOperation(Operation operation) {
        Preconditions.checkNotNull(operation, "operation cannot be null");
        this.operationService.execute(operation);
    }
}

