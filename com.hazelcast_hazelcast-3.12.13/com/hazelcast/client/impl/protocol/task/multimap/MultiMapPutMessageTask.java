/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapPutCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.PutOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapPutMessageTask
extends AbstractPartitionMessageTask<MultiMapPutCodec.RequestParameters> {
    public MultiMapPutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new PutOperation(((MultiMapPutCodec.RequestParameters)this.parameters).name, ((MultiMapPutCodec.RequestParameters)this.parameters).key, ((MultiMapPutCodec.RequestParameters)this.parameters).threadId, ((MultiMapPutCodec.RequestParameters)this.parameters).value, -1);
    }

    @Override
    protected MultiMapPutCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapPutCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapPutCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapPutCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapPutCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getMethodName() {
        return "put";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapPutCodec.RequestParameters)this.parameters).key, ((MultiMapPutCodec.RequestParameters)this.parameters).value};
    }
}

