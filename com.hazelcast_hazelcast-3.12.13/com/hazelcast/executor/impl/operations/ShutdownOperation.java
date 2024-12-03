/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl.operations;

import com.hazelcast.executor.impl.DistributedExecutorService;
import com.hazelcast.executor.impl.ExecutorDataSerializerHook;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.spi.impl.MutatingOperation;

public final class ShutdownOperation
extends AbstractNamedOperation
implements MutatingOperation,
IdentifiedDataSerializable {
    public ShutdownOperation() {
    }

    public ShutdownOperation(String name) {
        super(name);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:executorService";
    }

    @Override
    public void run() throws Exception {
        DistributedExecutorService service = (DistributedExecutorService)this.getService();
        service.shutdownExecutor(this.getName());
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public int getFactoryId() {
        return ExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }
}

