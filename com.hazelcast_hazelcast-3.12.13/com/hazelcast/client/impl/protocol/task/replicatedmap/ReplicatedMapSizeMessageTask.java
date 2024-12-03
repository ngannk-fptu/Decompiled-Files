/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapSizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.operation.SizeOperation;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ReplicatedMapSizeMessageTask
extends AbstractPartitionMessageTask<ReplicatedMapSizeCodec.RequestParameters> {
    public ReplicatedMapSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new SizeOperation(((ReplicatedMapSizeCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ReplicatedMapSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapSizeCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapSizeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapSizeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "size";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

