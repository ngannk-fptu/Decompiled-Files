/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.util.InvocationUtil;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Supplier;

public abstract class AbstractStableClusterMessageTask<P>
extends AbstractMessageTask<P>
implements ExecutionCallback {
    private static final int RETRY_COUNT = 100;

    protected AbstractStableClusterMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() throws Throwable {
        ICompletableFuture<Object> future = InvocationUtil.invokeOnStableClusterSerial(this.nodeEngine, this.createOperationSupplier(), 100);
        future.andThen(this);
    }

    abstract Supplier<Operation> createOperationSupplier();

    protected abstract Object resolve(Object var1);

    public final void onResponse(Object response) {
        this.sendResponse(this.resolve(response));
    }

    @Override
    public final void onFailure(Throwable t) {
        this.handleProcessingFailure(t);
    }
}

