/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;

public abstract class AbstractCallableMessageTask<P>
extends AbstractMessageTask<P> {
    protected AbstractCallableMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public final void processMessage() throws Exception {
        Object result = this.call();
        this.sendResponse(result);
    }

    protected abstract Object call() throws Exception;
}

