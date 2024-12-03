/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueOfferCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.OfferOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class QueueOfferMessageTask
extends AbstractPartitionMessageTask<QueueOfferCodec.RequestParameters> {
    public QueueOfferMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new OfferOperation(((QueueOfferCodec.RequestParameters)this.parameters).name, ((QueueOfferCodec.RequestParameters)this.parameters).timeoutMillis, ((QueueOfferCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueueOfferCodec.encodeResponse((Boolean)response);
    }

    @Override
    protected QueueOfferCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueOfferCodec.decodeRequest(clientMessage);
    }

    @Override
    public Object[] getParameters() {
        if (((QueueOfferCodec.RequestParameters)this.parameters).timeoutMillis > 0L) {
            return new Object[]{((QueueOfferCodec.RequestParameters)this.parameters).value, ((QueueOfferCodec.RequestParameters)this.parameters).timeoutMillis, TimeUnit.MILLISECONDS};
        }
        return new Object[]{((QueueOfferCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueOfferCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getMethodName() {
        return "offer";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueOfferCodec.RequestParameters)this.parameters).name;
    }
}

