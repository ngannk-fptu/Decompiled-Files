/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapValueCountCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.operations.CountOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapValueCountMessageTask
extends AbstractPartitionMessageTask<MultiMapValueCountCodec.RequestParameters> {
    public MultiMapValueCountMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CountOperation operation = new CountOperation(((MultiMapValueCountCodec.RequestParameters)this.parameters).name, ((MultiMapValueCountCodec.RequestParameters)this.parameters).key);
        operation.setThreadId(((MultiMapValueCountCodec.RequestParameters)this.parameters).threadId);
        return operation;
    }

    @Override
    protected MultiMapValueCountCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapValueCountCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapValueCountCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapValueCountCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapValueCountCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "valueCount";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapValueCountCodec.RequestParameters)this.parameters).key};
    }
}

