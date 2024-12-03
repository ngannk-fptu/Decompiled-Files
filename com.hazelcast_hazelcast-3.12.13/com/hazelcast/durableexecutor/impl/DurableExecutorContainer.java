/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl;

import com.hazelcast.durableexecutor.impl.TaskRingBuffer;
import com.hazelcast.durableexecutor.impl.operations.PutResultOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class DurableExecutorContainer {
    private final String name;
    private final NodeEngineImpl nodeEngine;
    private final InternalExecutionService executionService;
    private final ILogger logger;
    private final int partitionId;
    private final int durability;
    private final TaskRingBuffer ringBuffer;

    public DurableExecutorContainer(NodeEngineImpl nodeEngine, String name, int partitionId, int durability, TaskRingBuffer ringBuffer) {
        this.name = name;
        this.nodeEngine = nodeEngine;
        this.executionService = nodeEngine.getExecutionService();
        this.partitionId = partitionId;
        this.logger = nodeEngine.getLogger(DurableExecutorContainer.class);
        this.durability = durability;
        this.ringBuffer = ringBuffer;
    }

    public int execute(Callable callable) {
        int sequence = this.ringBuffer.add(callable);
        TaskProcessor processor = new TaskProcessor(sequence, callable);
        this.executionService.executeDurable(this.name, processor);
        return sequence;
    }

    public void putBackup(int sequence, Callable callable) {
        this.ringBuffer.putBackup(sequence, callable);
    }

    public Object retrieveResult(int sequence) {
        return this.ringBuffer.retrieve(sequence);
    }

    public void disposeResult(int sequence) {
        this.ringBuffer.dispose(sequence);
    }

    public Object retrieveAndDisposeResult(int sequence) {
        return this.ringBuffer.retrieveAndDispose(sequence);
    }

    public void putResult(int sequence, Object result) {
        this.ringBuffer.replaceTaskWithResult(sequence, result);
    }

    public boolean shouldWait(int sequence) {
        return this.ringBuffer.isTask(sequence);
    }

    void executeAll() {
        TaskRingBuffer.DurableIterator iterator = this.ringBuffer.iterator();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            boolean isCallable = iterator.isTask();
            if (!isCallable) continue;
            Callable callable = (Callable)item;
            int sequence = iterator.getSequence();
            TaskProcessor processor = new TaskProcessor(sequence, callable);
            this.executionService.executeDurable(this.name, processor);
        }
    }

    public TaskRingBuffer getRingBuffer() {
        return this.ringBuffer;
    }

    public int getDurability() {
        return this.durability;
    }

    public String getName() {
        return this.name;
    }

    public final class TaskProcessor
    extends FutureTask
    implements Runnable {
        private final String callableString;
        private final int sequence;

        private TaskProcessor(int sequence, Callable callable) {
            super(callable);
            this.callableString = String.valueOf(callable);
            this.sequence = sequence;
        }

        @Override
        public void run() {
            Exception response;
            block7: {
                response = null;
                try {
                    super.run();
                    if (this.isCancelled()) break block7;
                    response = (Exception)this.get();
                }
                catch (Exception e) {
                    try {
                        DurableExecutorContainer.this.logger.warning("While executing callable: " + this.callableString, e);
                        response = e;
                    }
                    catch (Throwable throwable) {
                        if (!this.isCancelled()) {
                            this.setResponse(response);
                        }
                        throw throwable;
                    }
                    if (!this.isCancelled()) {
                        this.setResponse(response);
                    }
                }
            }
            if (!this.isCancelled()) {
                this.setResponse(response);
            }
        }

        private void setResponse(Object response) {
            InternalOperationService operationService = DurableExecutorContainer.this.nodeEngine.getOperationService();
            PutResultOperation op = new PutResultOperation(DurableExecutorContainer.this.name, this.sequence, response);
            operationService.createInvocationBuilder("hz:impl:durableExecutorService", (Operation)op, DurableExecutorContainer.this.partitionId).setCallTimeout(Long.MAX_VALUE).invoke();
        }
    }
}

