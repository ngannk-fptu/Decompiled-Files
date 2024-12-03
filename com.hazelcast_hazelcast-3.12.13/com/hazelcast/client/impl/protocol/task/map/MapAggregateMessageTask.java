/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAggregateCodec;
import com.hazelcast.client.impl.protocol.task.map.DefaultMapAggregateMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;

public class MapAggregateMessageTask
extends DefaultMapAggregateMessageTask<MapAggregateCodec.RequestParameters> {
    public MapAggregateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Aggregator<?, ?> getAggregator() {
        return (Aggregator)this.nodeEngine.getSerializationService().toObject(((MapAggregateCodec.RequestParameters)this.parameters).aggregator);
    }

    @Override
    protected MapAggregateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAggregateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Object data = this.nodeEngine.getSerializationService().toData(response);
        return MapAggregateCodec.encodeResponse(data);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapAggregateCodec.RequestParameters)this.parameters).name, "aggregate");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAggregateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "aggregate";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapAggregateCodec.RequestParameters)this.parameters).name, ((MapAggregateCodec.RequestParameters)this.parameters).aggregator};
    }
}

