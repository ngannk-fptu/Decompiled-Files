/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreGetSemaphoreTypeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.config.cp.CPSemaphoreConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class GetSemaphoreTypeMessageTask
extends AbstractMessageTask<CPSemaphoreGetSemaphoreTypeCodec.RequestParameters> {
    public GetSemaphoreTypeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        CPSemaphoreConfig config = this.nodeEngine.getConfig().getCPSubsystemConfig().findSemaphoreConfig(((CPSemaphoreGetSemaphoreTypeCodec.RequestParameters)this.parameters).proxyName);
        boolean jdkCompatible = config != null && config.isJDKCompatible();
        this.sendResponse(jdkCompatible);
    }

    @Override
    protected CPSemaphoreGetSemaphoreTypeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSemaphoreGetSemaphoreTypeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSemaphoreGetSemaphoreTypeCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPSemaphoreGetSemaphoreTypeCodec.RequestParameters)this.parameters).proxyName;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

