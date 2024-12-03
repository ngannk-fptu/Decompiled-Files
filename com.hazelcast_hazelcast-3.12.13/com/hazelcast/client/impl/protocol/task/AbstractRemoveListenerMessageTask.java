/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;

public abstract class AbstractRemoveListenerMessageTask<P>
extends AbstractCallableMessageTask<P>
implements ListenerMessageTask {
    protected AbstractRemoveListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    public final Object call() {
        this.endpoint.removeDestroyAction(this.getRegistrationId());
        return this.deRegisterListener();
    }

    protected abstract boolean deRegisterListener();

    protected abstract String getRegistrationId();

    @Override
    public Object[] getParameters() {
        return new Object[]{this.getRegistrationId()};
    }
}

