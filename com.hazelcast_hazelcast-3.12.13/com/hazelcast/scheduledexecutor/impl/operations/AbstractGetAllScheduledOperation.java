/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainerHolder;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import java.util.Collection;
import java.util.List;

public abstract class AbstractGetAllScheduledOperation
extends AbstractSchedulerOperation {
    public AbstractGetAllScheduledOperation() {
    }

    public AbstractGetAllScheduledOperation(String schedulerName) {
        super(schedulerName);
    }

    protected void populateScheduledForHolder(List<ScheduledTaskHandler> handlers, DistributedScheduledExecutorService service, int holderId) {
        ScheduledExecutorContainerHolder partition = service.getPartitionOrMemberBin(holderId);
        ScheduledExecutorContainer container = partition.getContainer(this.schedulerName);
        if (container == null || service.isShutdown(this.schedulerName)) {
            return;
        }
        Collection<ScheduledTaskDescriptor> tasks = container.getTasks();
        for (ScheduledTaskDescriptor task : tasks) {
            handlers.add(container.offprintHandler(task.getDefinition().getName()));
        }
    }
}

