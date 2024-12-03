/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;

public abstract class AbstractInvocationMessageTask<P>
extends AbstractMessageTask<P>
implements ExecutionCallback {
    protected AbstractInvocationMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        Operation op = this.prepareOperation();
        op.setCallerUuid(this.endpoint.getUuid());
        InvocationBuilder builder = this.getInvocationBuilder(op).setExecutionCallback(this).setResultDeserialized(false);
        builder.invoke();
    }

    protected abstract InvocationBuilder getInvocationBuilder(Operation var1);

    protected abstract Operation prepareOperation();

    public void onResponse(Object response) {
        this.sendResponse(response);
    }

    @Override
    public void onFailure(Throwable t) {
        this.handleProcessingFailure(t);
    }
}

