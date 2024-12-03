/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapQueryMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultUtils;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.util.IterationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class DefaultMapProjectMessageTask<P>
extends AbstractMapQueryMessageTask<P, QueryResult, QueryResult, List<Data>> {
    public DefaultMapProjectMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected IterationType getIterationType() {
        return IterationType.VALUE;
    }

    @Override
    protected Aggregator<?, ?> getAggregator() {
        return null;
    }

    @Override
    protected Predicate getPredicate() {
        return TruePredicate.INSTANCE;
    }

    @Override
    protected void extractAndAppendResult(Collection<QueryResult> results, QueryResult result) {
        results.add(result);
    }

    @Override
    protected List<Data> reduce(Collection<QueryResult> results) {
        if (results.isEmpty()) {
            return Collections.emptyList();
        }
        QueryResult combinedResult = null;
        for (QueryResult result : results) {
            if (combinedResult == null) {
                combinedResult = result;
                continue;
            }
            combinedResult.combine(result);
        }
        Set result = QueryResultUtils.transformToSet(this.nodeEngine.getSerializationService(), combinedResult, this.getPredicate(), IterationType.VALUE, false, true);
        ArrayList<Data> serialized = new ArrayList<Data>(result.size());
        for (Object row : result) {
            serialized.add((Data)row);
        }
        return serialized;
    }
}

