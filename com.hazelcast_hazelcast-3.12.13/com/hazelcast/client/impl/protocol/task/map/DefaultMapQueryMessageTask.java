/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapQueryMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.nio.Connection;
import com.hazelcast.projection.Projection;
import java.util.Collection;

public abstract class DefaultMapQueryMessageTask<P>
extends AbstractMapQueryMessageTask<P, QueryResult, QueryResultRow, Object> {
    protected DefaultMapQueryMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Aggregator<?, ?> getAggregator() {
        return null;
    }

    @Override
    protected Projection<?, ?> getProjection() {
        return null;
    }

    @Override
    protected void extractAndAppendResult(Collection<QueryResultRow> results, QueryResult queryResult) {
        results.addAll(queryResult.getRows());
    }
}

