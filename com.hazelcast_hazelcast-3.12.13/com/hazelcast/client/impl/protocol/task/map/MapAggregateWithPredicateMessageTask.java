/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAggregateWithPredicateCodec;
import com.hazelcast.client.impl.protocol.task.map.DefaultMapAggregateMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.query.Predicate;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;

public class MapAggregateWithPredicateMessageTask
extends DefaultMapAggregateMessageTask<MapAggregateWithPredicateCodec.RequestParameters> {
    public MapAggregateWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Aggregator<?, ?> getAggregator() {
        return (Aggregator)this.nodeEngine.getSerializationService().toObject(((MapAggregateWithPredicateCodec.RequestParameters)this.parameters).aggregator);
    }

    @Override
    protected Predicate getPredicate() {
        return (Predicate)this.nodeEngine.getSerializationService().toObject(((MapAggregateWithPredicateCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected MapAggregateWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAggregateWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Object data = this.nodeEngine.getSerializationService().toData(response);
        return MapAggregateWithPredicateCodec.encodeResponse(data);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapAggregateWithPredicateCodec.RequestParameters)this.parameters).name, "aggregate");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAggregateWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "aggregateWithPredicate";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapAggregateWithPredicateCodec.RequestParameters)this.parameters).name, ((MapAggregateWithPredicateCodec.RequestParameters)this.parameters).aggregator, ((MapAggregateWithPredicateCodec.RequestParameters)this.parameters).predicate};
    }
}

