/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapValuesCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapValueCollection;
import com.hazelcast.replicatedmap.impl.operation.ValuesOperation;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.List;

public class ReplicatedMapValuesMessageTask
extends AbstractPartitionMessageTask<ReplicatedMapValuesCodec.RequestParameters> {
    public ReplicatedMapValuesMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ValuesOperation(((ReplicatedMapValuesCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ReplicatedMapValuesCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapValuesCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        ReplicatedMapValueCollection values = (ReplicatedMapValueCollection)response;
        return ReplicatedMapValuesCodec.encodeResponse((List)values.getValues());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapValuesCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapValuesCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "values";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

