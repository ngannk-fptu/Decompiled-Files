/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapKeySetWithPredicateCodec;
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

public class MapKeySetWithPredicateMessageTask
extends DefaultMapQueryMessageTask<MapKeySetWithPredicateCodec.RequestParameters> {
    public MapKeySetWithPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
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
        return (Predicate)this.serializationService.toObject(((MapKeySetWithPredicateCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected IterationType getIterationType() {
        return IterationType.KEY;
    }

    @Override
    protected MapKeySetWithPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapKeySetWithPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapKeySetWithPredicateCodec.encodeResponse((List)response);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapKeySetWithPredicateCodec.RequestParameters)this.parameters).predicate};
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapKeySetWithPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "keySet";
    }
}

