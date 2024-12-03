/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.operations.OperationFactoryWrapper;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.util.Map;

public abstract class AbstractAllPartitionsMessageTask<P>
extends AbstractMessageTask<P>
implements ExecutionCallback<Map<Integer, Object>> {
    public AbstractAllPartitionsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        OperationFactoryWrapper operationFactory = new OperationFactoryWrapper(this.createOperationFactory(), this.endpoint.getUuid());
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        operationService.invokeOnAllPartitionsAsync(this.getServiceName(), operationFactory).andThen(this);
    }

    protected abstract OperationFactory createOperationFactory();

    protected abstract Object reduce(Map<Integer, Object> var1);

    @Override
    public final void onFailure(Throwable throwable) {
        this.handleProcessingFailure(throwable);
    }

    @Override
    public final void onResponse(Map<Integer, Object> map) {
        try {
            this.sendResponse(this.reduce(map));
        }
        catch (Exception e) {
            this.handleProcessingFailure(e);
        }
    }
}

