/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationexecutor.OperationRunnerFactory;
import com.hazelcast.spi.impl.operationservice.impl.OperationRunnerImpl;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;

class OperationRunnerFactoryImpl
implements OperationRunnerFactory {
    private OperationServiceImpl operationService;
    private int genericId;

    OperationRunnerFactoryImpl(OperationServiceImpl operationService) {
        this.operationService = operationService;
    }

    @Override
    public OperationRunner createAdHocRunner() {
        return new OperationRunnerImpl(this.operationService, -2, 0, null);
    }

    @Override
    public OperationRunner createPartitionRunner(int partitionId) {
        return new OperationRunnerImpl(this.operationService, partitionId, 0, this.operationService.failedBackupsCount);
    }

    @Override
    public OperationRunner createGenericRunner() {
        return new OperationRunnerImpl(this.operationService, -1, this.genericId++, null);
    }
}

