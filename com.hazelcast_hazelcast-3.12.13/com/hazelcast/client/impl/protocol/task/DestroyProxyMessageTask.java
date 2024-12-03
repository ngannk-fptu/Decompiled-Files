/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientDestroyProxyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.BlockingMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.spi.impl.proxyservice.InternalProxyService;
import java.security.Permission;

public class DestroyProxyMessageTask
extends AbstractCallableMessageTask<ClientDestroyProxyCodec.RequestParameters>
implements BlockingMessageTask {
    public DestroyProxyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        InternalProxyService proxyService = this.nodeEngine.getProxyService();
        proxyService.destroyDistributedObject(((ClientDestroyProxyCodec.RequestParameters)this.parameters).serviceName, ((ClientDestroyProxyCodec.RequestParameters)this.parameters).name);
        return null;
    }

    @Override
    protected ClientDestroyProxyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientDestroyProxyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientDestroyProxyCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return ((ClientDestroyProxyCodec.RequestParameters)this.parameters).serviceName;
    }

    @Override
    public Permission getRequiredPermission() {
        return ActionConstants.getPermission(((ClientDestroyProxyCodec.RequestParameters)this.parameters).name, ((ClientDestroyProxyCodec.RequestParameters)this.parameters).serviceName, "destroy");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ClientDestroyProxyCodec.RequestParameters)this.parameters).name;
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

