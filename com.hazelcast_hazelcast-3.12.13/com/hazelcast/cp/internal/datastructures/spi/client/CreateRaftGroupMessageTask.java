/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPGroupCreateCPGroupCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class CreateRaftGroupMessageTask
extends AbstractMessageTask<CPGroupCreateCPGroupCodec.RequestParameters>
implements ExecutionCallback {
    public CreateRaftGroupMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        RaftService service = (RaftService)this.nodeEngine.getService("hz:core:raft");
        service.createRaftGroupForProxyAsync(((CPGroupCreateCPGroupCodec.RequestParameters)this.parameters).proxyName).andThen(this);
    }

    public void onResponse(Object response) {
        this.sendResponse(response);
    }

    @Override
    public void onFailure(Throwable t) {
        this.handleProcessingFailure(t);
    }

    @Override
    protected CPGroupCreateCPGroupCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPGroupCreateCPGroupCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPGroupCreateCPGroupCodec.encodeResponse((RaftGroupId)response);
    }

    @Override
    public String getServiceName() {
        return "hz:core:raft";
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPGroupCreateCPGroupCodec.RequestParameters)this.parameters).proxyName;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "createRaftGroup";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPGroupCreateCPGroupCodec.RequestParameters)this.parameters).proxyName};
    }
}

