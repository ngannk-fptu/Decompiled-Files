/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapPutCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.operation.PutOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class ReplicatedMapPutMessageTask
extends AbstractPartitionMessageTask<ReplicatedMapPutCodec.RequestParameters> {
    public ReplicatedMapPutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new PutOperation(((ReplicatedMapPutCodec.RequestParameters)this.parameters).name, ((ReplicatedMapPutCodec.RequestParameters)this.parameters).key, ((ReplicatedMapPutCodec.RequestParameters)this.parameters).value, ((ReplicatedMapPutCodec.RequestParameters)this.parameters).ttl);
    }

    @Override
    protected ReplicatedMapPutCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapPutCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        VersionResponsePair versionResponsePair = (VersionResponsePair)response;
        return ReplicatedMapPutCodec.encodeResponse(this.serializationService.toData(versionResponsePair.getResponse()));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapPutCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "put";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapPutCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public Object[] getParameters() {
        if (((ReplicatedMapPutCodec.RequestParameters)this.parameters).ttl > 0L) {
            return new Object[]{((ReplicatedMapPutCodec.RequestParameters)this.parameters).key, ((ReplicatedMapPutCodec.RequestParameters)this.parameters).value, ((ReplicatedMapPutCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS};
        }
        return new Object[]{((ReplicatedMapPutCodec.RequestParameters)this.parameters).key, ((ReplicatedMapPutCodec.RequestParameters)this.parameters).value};
    }
}

