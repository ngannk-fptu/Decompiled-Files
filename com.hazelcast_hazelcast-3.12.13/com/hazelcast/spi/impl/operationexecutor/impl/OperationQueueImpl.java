/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor.impl;

import com.hazelcast.spi.impl.operationexecutor.impl.OperationQueue;
import com.hazelcast.util.Preconditions;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class OperationQueueImpl
implements OperationQueue {
    static final Object TRIGGER_TASK = new Object(){

        public String toString() {
            return "triggerTask";
        }
    };
    private final BlockingQueue<Object> normalQueue;
    private final Queue<Object> priorityQueue;

    public OperationQueueImpl() {
        this(new LinkedBlockingQueue<Object>(), new ConcurrentLinkedQueue<Object>());
    }

    public OperationQueueImpl(BlockingQueue<Object> normalQueue, Queue<Object> priorityQueue) {
        this.normalQueue = Preconditions.checkNotNull(normalQueue, "normalQueue");
        this.priorityQueue = Preconditions.checkNotNull(priorityQueue, "priorityQueue");
    }

    @Override
    public int normalSize() {
        return this.normalQueue.size();
    }

    @Override
    public int prioritySize() {
        return this.priorityQueue.size();
    }

    @Override
    public int size() {
        return this.normalQueue.size() + this.priorityQueue.size();
    }

    @Override
    public void add(Object task, boolean priority) {
        Preconditions.checkNotNull(task, "task can't be null");
        if (priority) {
            this.priorityQueue.add(task);
            this.normalQueue.add(TRIGGER_TASK);
        } else {
            this.normalQueue.add(task);
        }
    }

    @Override
    public Object take(boolean priorityOnly) throws InterruptedException {
        Object normalItem;
        if (priorityOnly) {
            return ((BlockingQueue)this.priorityQueue).take();
        }
        do {
            Object priorityItem;
            if ((priorityItem = this.priorityQueue.poll()) == null) continue;
            return priorityItem;
        } while ((normalItem = this.normalQueue.take()) == TRIGGER_TASK);
        return normalItem;
    }
}

