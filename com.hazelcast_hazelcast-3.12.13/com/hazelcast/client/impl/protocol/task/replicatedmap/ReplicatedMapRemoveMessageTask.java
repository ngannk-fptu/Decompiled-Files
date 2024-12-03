/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapRemoveCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.operation.RemoveOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ReplicatedMapRemoveMessageTask
extends AbstractPartitionMessageTask<ReplicatedMapRemoveCodec.RequestParameters> {
    public ReplicatedMapRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new RemoveOperation(((ReplicatedMapRemoveCodec.RequestParameters)this.parameters).name, ((ReplicatedMapRemoveCodec.RequestParameters)this.parameters).key);
    }

    @Override
    protected ReplicatedMapRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        VersionResponsePair versionResponsePair = (VersionResponsePair)response;
        return ReplicatedMapRemoveCodec.encodeResponse(this.serializationService.toData(versionResponsePair.getResponse()));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ReplicatedMapRemoveCodec.RequestParameters)this.parameters).key};
    }
}

