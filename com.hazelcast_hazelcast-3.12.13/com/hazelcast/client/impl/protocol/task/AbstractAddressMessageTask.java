/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.operationexecutor.impl.PartitionOperationThread;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.concurrent.Executor;

public abstract class AbstractAddressMessageTask<P>
extends AbstractMessageTask<P>
implements ExecutionCallback,
Executor {
    protected AbstractAddressMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    protected void beforeProcess() {
    }

    protected void beforeResponse() {
    }

    protected void afterResponse() {
    }

    @Override
    public final void processMessage() {
        this.beforeProcess();
        Operation op = this.prepareOperation();
        op.setCallerUuid(this.endpoint.getUuid());
        InternalCompletableFuture f = this.nodeEngine.getOperationService().createInvocationBuilder(this.getServiceName(), op, this.getAddress()).setResultDeserialized(false).invoke();
        f.andThen(this, this);
    }

    protected abstract Address getAddress();

    protected abstract Operation prepareOperation();

    @Override
    public void execute(Runnable command) {
        if (Thread.currentThread().getClass() == PartitionOperationThread.class) {
            command.run();
        } else {
            InternalExecutionService executionService = this.nodeEngine.getExecutionService();
            ManagedExecutorService executor = executionService.getExecutor("hz:async");
            executor.execute(command);
        }
    }

    public void onResponse(Object response) {
        this.beforeResponse();
        this.sendResponse(response);
        this.afterResponse();
    }

    @Override
    public void onFailure(Throwable t) {
        this.beforeResponse();
        this.handleProcessingFailure(t);
        this.afterResponse();
    }
}

