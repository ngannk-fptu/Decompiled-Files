/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.TransactionalMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.util.ThreadUtil;

public abstract class AbstractTransactionalMessageTask<P>
extends AbstractCallableMessageTask<P>
implements TransactionalMessageTask {
    public AbstractTransactionalMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected final Object call() throws Exception {
        ThreadUtil.setThreadId(this.getClientThreadId());
        try {
            Object object = this.innerCall();
            return object;
        }
        finally {
            ThreadUtil.removeThreadId();
        }
    }

    protected abstract Object innerCall() throws Exception;

    protected abstract long getClientThreadId();
}

