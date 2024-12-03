/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueDrainToMaxSizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.DrainOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.SerializableList;
import java.security.Permission;
import java.util.List;

public class QueueDrainMaxSizeMessageTask
extends AbstractPartitionMessageTask<QueueDrainToMaxSizeCodec.RequestParameters> {
    public QueueDrainMaxSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new DrainOperation(((QueueDrainToMaxSizeCodec.RequestParameters)this.parameters).name, ((QueueDrainToMaxSizeCodec.RequestParameters)this.parameters).maxSize);
    }

    @Override
    protected QueueDrainToMaxSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueDrainToMaxSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueDrainToMaxSizeCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "drainTo";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null, ((QueueDrainToMaxSizeCodec.RequestParameters)this.parameters).maxSize};
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        SerializableList serializableList = (SerializableList)response;
        List<Data> coll = serializableList.getCollection();
        return QueueDrainToMaxSizeCodec.encodeResponse(coll);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueDrainToMaxSizeCodec.RequestParameters)this.parameters).name;
    }
}

