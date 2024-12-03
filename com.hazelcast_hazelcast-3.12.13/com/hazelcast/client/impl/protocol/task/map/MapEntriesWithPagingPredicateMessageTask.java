/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapEntriesWithPagingPredicateCodec;
import com.hazelcast.client.impl.protocol.task.map.DefaultMapQueryMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.nio.Connection;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.IterationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapEntriesWithPagingPredicateMessageTask
extends DefaultMapQueryMessageTask<MapEntriesWithPagingPredicateCodec.RequestParameters> {
    public MapEntriesWithPagingPredicateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object reduce(Collection<QueryResultRow> result) {
        ArrayList<QueryResultRow> entries = new ArrayList<QueryResultRow>(result.size());
        for (QueryResultRow resultRow : result) {
            entries.add(resultRow);
        }
        return entries;
    }

    @Override
    protected Predicate getPredicate() {
        return (Predicate)this.serializationService.toObject(((MapEntriesWithPagingPredicateCodec.RequestParameters)this.parameters).predicate);
    }

    @Override
    protected IterationType getIterationType() {
        return IterationType.ENTRY;
    }

    @Override
    protected MapEntriesWithPagingPredicateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapEntriesWithPagingPredicateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapEntriesWithPagingPredicateCodec.encodeResponse((List)response);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapEntriesWithPagingPredicateCodec.RequestParameters)this.parameters).predicate};
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapEntriesWithPagingPredicateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "entrySet";
    }
}

