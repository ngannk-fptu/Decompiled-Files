/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueIteratorCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.IteratorOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SerializableList;
import java.security.Permission;
import java.util.List;

public class QueueIteratorMessageTask
extends AbstractPartitionMessageTask<QueueIteratorCodec.RequestParameters> {
    public QueueIteratorMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new IteratorOperation(((QueueIteratorCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected QueueIteratorCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueIteratorCodec.decodeRequest(clientMessage);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueIteratorCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "iterator";
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        SerializableList serializableList = (SerializableList)response;
        List<Data> coll = serializableList.getCollection();
        return QueueIteratorCodec.encodeResponse(coll);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueIteratorCodec.RequestParameters)this.parameters).name;
    }
}

