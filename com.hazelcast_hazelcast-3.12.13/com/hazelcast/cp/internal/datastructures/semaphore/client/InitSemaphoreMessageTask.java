/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreInitCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.InitSemaphoreOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import java.security.Permission;

public class InitSemaphoreMessageTask
extends AbstractCPMessageTask<CPSemaphoreInitCodec.RequestParameters> {
    public InitSemaphoreMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPSemaphoreInitCodec.RequestParameters)this.parameters).groupId, new InitSemaphoreOp(((CPSemaphoreInitCodec.RequestParameters)this.parameters).name, ((CPSemaphoreInitCodec.RequestParameters)this.parameters).permits));
    }

    @Override
    protected CPSemaphoreInitCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSemaphoreInitCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSemaphoreInitCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((CPSemaphoreInitCodec.RequestParameters)this.parameters).name, "release");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPSemaphoreInitCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "init";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPSemaphoreInitCodec.RequestParameters)this.parameters).permits};
    }
}

