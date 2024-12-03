/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.operations.OperationFactoryWrapper;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractMultiPartitionMessageTask<P>
extends AbstractMessageTask<P>
implements ExecutionCallback<Map<Integer, Object>> {
    protected AbstractMultiPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        OperationFactoryWrapper operationFactory = new OperationFactoryWrapper(this.createOperationFactory(), this.endpoint.getUuid());
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        operationService.invokeOnPartitionsAsync(this.getServiceName(), operationFactory, this.getPartitions()).andThen(this);
    }

    protected final MapOperationProvider getMapOperationProvider(String mapName) {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        return mapServiceContext.getMapOperationProvider(mapName);
    }

    public abstract Collection<Integer> getPartitions();

    protected abstract OperationFactory createOperationFactory();

    protected abstract Object reduce(Map<Integer, Object> var1);

    @Override
    public final void onFailure(Throwable throwable) {
        this.handleProcessingFailure(throwable);
    }

    @Override
    public final void onResponse(Map<Integer, Object> map) {
        this.sendResponse(this.reduce(map));
    }
}

