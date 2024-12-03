/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientCreateProxyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.proxyservice.impl.operations.InitializeDistributedObjectOperation;
import java.security.Permission;
import java.util.Collection;

public class CreateProxyMessageTask
extends AbstractInvocationMessageTask<ClientCreateProxyCodec.RequestParameters>
implements BlockingMessageTask {
    public CreateProxyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((ClientCreateProxyCodec.RequestParameters)this.parameters).target).setTryCount(1);
    }

    @Override
    protected Operation prepareOperation() {
        return new InitializeDistributedObjectOperation(((ClientCreateProxyCodec.RequestParameters)this.parameters).serviceName, ((ClientCreateProxyCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ClientCreateProxyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = ClientCreateProxyCodec.decodeRequest(clientMessage);
        ((ClientCreateProxyCodec.RequestParameters)this.parameters).target = this.clientEngine.memberAddressOf(((ClientCreateProxyCodec.RequestParameters)this.parameters).target);
        return (ClientCreateProxyCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientCreateProxyCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return ((ClientCreateProxyCodec.RequestParameters)this.parameters).serviceName;
    }

    @Override
    public Permission getRequiredPermission() {
        ProxyService proxyService = this.clientEngine.getProxyService();
        Collection<String> distributedObjectNames = proxyService.getDistributedObjectNames(((ClientCreateProxyCodec.RequestParameters)this.parameters).serviceName);
        if (distributedObjectNames.contains(((ClientCreateProxyCodec.RequestParameters)this.parameters).name)) {
            return null;
        }
        return ActionConstants.getPermission(((ClientCreateProxyCodec.RequestParameters)this.parameters).name, ((ClientCreateProxyCodec.RequestParameters)this.parameters).serviceName, "create");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ClientCreateProxyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

