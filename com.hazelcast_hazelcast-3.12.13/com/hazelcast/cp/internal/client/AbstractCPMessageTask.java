/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InternalCompletableFuture;

public abstract class AbstractCPMessageTask<P>
extends AbstractMessageTask<P>
implements ExecutionCallback<Object> {
    protected AbstractCPMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    protected void query(CPGroupId groupId, RaftOp op, QueryPolicy policy) {
        RaftInvocationManager invocationManager = this.getInvocationManager();
        InternalCompletableFuture future = invocationManager.query(groupId, op, policy, false);
        future.andThen(this);
    }

    protected void invoke(CPGroupId groupId, RaftOp op) {
        RaftInvocationManager invocationManager = this.getInvocationManager();
        InternalCompletableFuture future = invocationManager.invoke(groupId, op, false);
        future.andThen(this);
    }

    private RaftInvocationManager getInvocationManager() {
        RaftService service = (RaftService)this.nodeEngine.getService("hz:core:raft");
        return service.getInvocationManager();
    }

    @Override
    public void onResponse(Object response) {
        this.sendResponse(response);
    }

    @Override
    public void onFailure(Throwable t) {
        this.handleProcessingFailure(t);
    }
}

