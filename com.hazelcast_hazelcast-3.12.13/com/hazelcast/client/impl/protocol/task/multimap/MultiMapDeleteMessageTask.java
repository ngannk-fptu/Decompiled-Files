/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapDeleteCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.DeleteOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapDeleteMessageTask
extends AbstractPartitionMessageTask<MultiMapDeleteCodec.RequestParameters> {
    public MultiMapDeleteMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new DeleteOperation(((MultiMapDeleteCodec.RequestParameters)this.parameters).name, ((MultiMapDeleteCodec.RequestParameters)this.parameters).key, ((MultiMapDeleteCodec.RequestParameters)this.parameters).threadId);
    }

    @Override
    protected MultiMapDeleteCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapDeleteCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapDeleteCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapDeleteCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapDeleteCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "delete";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapDeleteCodec.RequestParameters)this.parameters).key};
    }
}

