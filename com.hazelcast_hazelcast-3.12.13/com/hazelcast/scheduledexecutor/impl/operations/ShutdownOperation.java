/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.spi.impl.MutatingOperation;

public class ShutdownOperation
extends AbstractSchedulerOperation
implements MutatingOperation {
    public ShutdownOperation() {
    }

    public ShutdownOperation(String schedulerName) {
        super(schedulerName);
    }

    @Override
    public void run() throws Exception {
        ((DistributedScheduledExecutorService)this.getService()).shutdownExecutor(this.schedulerName);
    }

    @Override
    public int getId() {
        return 23;
    }
}

