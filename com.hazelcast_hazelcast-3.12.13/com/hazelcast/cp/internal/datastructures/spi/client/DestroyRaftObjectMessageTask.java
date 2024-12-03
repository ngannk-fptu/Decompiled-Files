/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPGroupDestroyCPObjectCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.spi.operation.DestroyRaftObjectOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class DestroyRaftObjectMessageTask
extends AbstractCPMessageTask<CPGroupDestroyCPObjectCodec.RequestParameters> {
    public DestroyRaftObjectMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPGroupDestroyCPObjectCodec.RequestParameters)this.parameters).groupId, new DestroyRaftObjectOp(((CPGroupDestroyCPObjectCodec.RequestParameters)this.parameters).serviceName, ((CPGroupDestroyCPObjectCodec.RequestParameters)this.parameters).objectName));
    }

    @Override
    protected CPGroupDestroyCPObjectCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPGroupDestroyCPObjectCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPGroupDestroyCPObjectCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return ((CPGroupDestroyCPObjectCodec.RequestParameters)this.parameters).serviceName;
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPGroupDestroyCPObjectCodec.RequestParameters)this.parameters).objectName;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "destroyRaftObject";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPGroupDestroyCPObjectCodec.RequestParameters)this.parameters).serviceName, ((CPGroupDestroyCPObjectCodec.RequestParameters)this.parameters).objectName};
    }
}

