/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor;

import com.hazelcast.spi.impl.operationexecutor.OperationRunner;

public interface OperationRunnerFactory {
    public OperationRunner createPartitionRunner(int var1);

    public OperationRunner createGenericRunner();

    public OperationRunner createAdHocRunner();
}

