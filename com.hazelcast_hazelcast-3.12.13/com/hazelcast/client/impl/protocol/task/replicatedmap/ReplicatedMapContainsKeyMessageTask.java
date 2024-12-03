/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.operation.ContainsKeyOperation;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ReplicatedMapContainsKeyMessageTask
extends AbstractPartitionMessageTask<ReplicatedMapContainsKeyCodec.RequestParameters> {
    public ReplicatedMapContainsKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ContainsKeyOperation(((ReplicatedMapContainsKeyCodec.RequestParameters)this.parameters).name, ((ReplicatedMapContainsKeyCodec.RequestParameters)this.parameters).key);
    }

    @Override
    protected ReplicatedMapContainsKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapContainsKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapContainsKeyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapContainsKeyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapContainsKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "containsKey";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ReplicatedMapContainsKeyCodec.RequestParameters)this.parameters).key};
    }
}

