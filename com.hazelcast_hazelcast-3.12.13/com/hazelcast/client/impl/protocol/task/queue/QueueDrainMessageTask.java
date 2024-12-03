/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueDrainToCodec;
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

public class QueueDrainMessageTask
extends AbstractPartitionMessageTask<QueueDrainToCodec.RequestParameters> {
    public QueueDrainMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new DrainOperation(((QueueDrainToCodec.RequestParameters)this.parameters).name, -1);
    }

    @Override
    protected QueueDrainToCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueDrainToCodec.decodeRequest(clientMessage);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueDrainToCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "drainTo";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null};
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        SerializableList serializableList = (SerializableList)response;
        List<Data> coll = serializableList.getCollection();
        return QueueDrainToCodec.encodeResponse(coll);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueDrainToCodec.RequestParameters)this.parameters).name;
    }
}

