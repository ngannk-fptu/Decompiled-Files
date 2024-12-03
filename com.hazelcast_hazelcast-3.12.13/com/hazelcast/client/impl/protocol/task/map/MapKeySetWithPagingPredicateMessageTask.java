/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapKeySetWithPagingPredicateCodec;
import com.hazelcast.client.impl.protocol.task.map.DefaultMapQueryMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.IterationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapKeySetWithPagingPredicateMessageTask
extends DefaultMapQueryMessageTask<MapKeySetWithPagingPredicateCodec.RequestParameters> {
    public MapKeySetWithPagingPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object reduce(Collection<QueryResultRow> result) {
        ArrayList<Data> set = new ArrayList<Data>(result.size());
        for (QueryResultRow resultEntry : result) {
            set.add(resultEntry.getKey());
        }
        return set;
    }

    @Override
    protected Predicate getPredicate() {
        return (Predicate)this.serializationService.toObject(((MapKeySetWithPagingPredicateCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected IterationType getIterationType() {
        return IterationType.KEY;
    }

    @Override
    protected MapKeySetWithPagingPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapKeySetWithPagingPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapKeySetWithPagingPredicateCodec.encodeResponse((List)response);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapKeySetWithPagingPredicateCodec.RequestParameters)this.parameters).predicate};
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapKeySetWithPagingPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "keySet";
    }
}

