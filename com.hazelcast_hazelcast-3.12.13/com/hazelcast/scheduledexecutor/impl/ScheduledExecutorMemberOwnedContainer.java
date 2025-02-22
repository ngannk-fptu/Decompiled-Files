/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduledExecutorMemberOwnedContainer
extends ScheduledExecutorContainer {
    private static final int MEMBER_DURABILITY = 0;
    private final AtomicBoolean memberPartitionLock = new AtomicBoolean();

    ScheduledExecutorMemberOwnedContainer(String name, int capacity, NodeEngine nodeEngine) {
        super(name, -1, nodeEngine, 0, capacity, new ConcurrentHashMap<String, ScheduledTaskDescriptor>());
    }

    @Override
    public ScheduledFuture schedule(TaskDefinition definition) {
        try {
            this.acquireMemberPartitionLockIfNeeded();
            this.checkNotDuplicateTask(definition.getName());
            this.checkNotAtCapacity();
            ScheduledFuture scheduledFuture = this.createContextAndSchedule(definition);
            return scheduledFuture;
        }
        finally {
            this.releaseMemberPartitionLockIfNeeded();
        }
    }

    @Override
    public boolean shouldParkGetResult(String taskName) {
        return false;
    }

    @Override
    public ScheduledTaskHandler offprintHandler(String taskName) {
        return ScheduledTaskHandlerImpl.of(this.getNodeEngine().getThisAddress(), this.getName(), taskName);
    }

    @Override
    protected InvocationBuilder createInvocationBuilder(Operation op) {
        OperationService operationService = this.getNodeEngine().getOperationService();
        return operationService.createInvocationBuilder("hz:impl:scheduledExecutorService", op, this.getNodeEngine().getThisAddress());
    }

    private void acquireMemberPartitionLockIfNeeded() {
        while (!this.memberPartitionLock.compareAndSet(false, true)) {
            Thread.yield();
        }
    }

    private void releaseMemberPartitionLockIfNeeded() {
        this.memberPartitionLock.set(false);
    }
}

