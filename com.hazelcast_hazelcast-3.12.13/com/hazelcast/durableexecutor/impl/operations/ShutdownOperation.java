/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.DistributedDurableExecutorService;
import com.hazelcast.durableexecutor.impl.operations.AbstractDurableExecutorOperation;
import com.hazelcast.spi.impl.MutatingOperation;

public class ShutdownOperation
extends AbstractDurableExecutorOperation
implements MutatingOperation {
    public ShutdownOperation() {
    }

    public ShutdownOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        DistributedDurableExecutorService service = (DistributedDurableExecutorService)this.getService();
        service.shutdownExecutor(this.name);
    }

    @Override
    public int getId() {
        return 6;
    }
}

