/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractGetAllScheduledOperation;
import com.hazelcast.spi.ReadonlyOperation;
import java.util.ArrayList;
import java.util.List;

public class GetAllScheduledOnPartitionOperation
extends AbstractGetAllScheduledOperation
implements ReadonlyOperation {
    private List<ScheduledTaskHandler> response;

    public GetAllScheduledOnPartitionOperation() {
    }

    public GetAllScheduledOnPartitionOperation(String schedulerName) {
        super(schedulerName);
    }

    @Override
    public void run() throws Exception {
        ArrayList<ScheduledTaskHandler> handlers = new ArrayList<ScheduledTaskHandler>();
        DistributedScheduledExecutorService service = (DistributedScheduledExecutorService)this.getService();
        this.populateScheduledForHolder(handlers, service, this.getPartitionId());
        this.response = handlers;
    }

    @Override
    public List<ScheduledTaskHandler> getResponse() {
        return this.response;
    }

    @Override
    public int getId() {
        return 25;
    }
}

