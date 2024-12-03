/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.aggregation.impl.CanonicalizingHashSet;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapQueryMessageTask;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.nio.Connection;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.util.IterationType;
import java.util.Collection;
import java.util.HashSet;

public abstract class DefaultMapAggregateMessageTask<P>
extends AbstractMapQueryMessageTask<P, AggregationResult, AggregationResult, Object> {
    private static final int MIXED_TYPES_VERSION = BuildInfo.calculateVersion("3.12");

    public DefaultMapAggregateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected IterationType getIterationType() {
        return IterationType.ENTRY;
    }

    @Override
    protected Projection<?, ?> getProjection() {
        return null;
    }

    @Override
    protected Predicate getPredicate() {
        return TruePredicate.INSTANCE;
    }

    @Override
    protected void extractAndAppendResult(Collection<AggregationResult> results, AggregationResult aggregationResult) {
        results.add(aggregationResult);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object reduce(Collection<AggregationResult> results) {
        if (results.isEmpty()) {
            return null;
        }
        AggregationResult combinedResult = null;
        try {
            for (AggregationResult result : results) {
                if (combinedResult == null) {
                    combinedResult = result;
                    continue;
                }
                combinedResult.combine(result);
            }
        }
        finally {
            if (combinedResult != null) {
                combinedResult.onCombineFinished();
            }
        }
        if (combinedResult == null) {
            return null;
        }
        Object result = combinedResult.getAggregator().aggregate();
        if (result instanceof CanonicalizingHashSet && this.endpoint.getClientVersion() < MIXED_TYPES_VERSION) {
            return new HashSet((CanonicalizingHashSet)result);
        }
        return result;
    }
}

