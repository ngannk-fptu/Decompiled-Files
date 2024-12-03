/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapKeySetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapKeys;
import com.hazelcast.replicatedmap.impl.operation.KeySetOperation;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ReplicatedMapKeySetMessageTask
extends AbstractPartitionMessageTask<ReplicatedMapKeySetCodec.RequestParameters> {
    public ReplicatedMapKeySetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new KeySetOperation(((ReplicatedMapKeySetCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ReplicatedMapKeySetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapKeySetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        ReplicatedMapKeys keySet = (ReplicatedMapKeys)response;
        return ReplicatedMapKeySetCodec.encodeResponse(keySet.getKeys());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapKeySetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapKeySetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "keySet";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

