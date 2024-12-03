/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapIsEmptyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.operation.IsEmptyOperation;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ReplicatedMapIsEmptyMessageTask
extends AbstractPartitionMessageTask<ReplicatedMapIsEmptyCodec.RequestParameters> {
    public ReplicatedMapIsEmptyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new IsEmptyOperation(((ReplicatedMapIsEmptyCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ReplicatedMapIsEmptyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapIsEmptyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapIsEmptyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(((ReplicatedMapIsEmptyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ReplicatedMapIsEmptyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "isEmpty";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

