/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryMadePublishableCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.querycache.subscriber.operation.MadePublishableOperationFactory;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class MapMadePublishableMessageTask
extends AbstractAllPartitionsMessageTask<ContinuousQueryMadePublishableCodec.RequestParameters> {
    public MapMadePublishableMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected ContinuousQueryMadePublishableCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ContinuousQueryMadePublishableCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ContinuousQueryMadePublishableCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((ContinuousQueryMadePublishableCodec.RequestParameters)this.parameters).mapName;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new MadePublishableOperationFactory(((ContinuousQueryMadePublishableCodec.RequestParameters)this.parameters).mapName, ((ContinuousQueryMadePublishableCodec.RequestParameters)this.parameters).cacheName);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        return !map.containsValue(Boolean.FALSE);
    }
}

