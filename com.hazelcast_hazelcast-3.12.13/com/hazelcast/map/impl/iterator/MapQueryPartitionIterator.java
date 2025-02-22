/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.iterator;

import com.hazelcast.map.impl.iterator.AbstractMapQueryPartitionIterator;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.map.impl.query.ResultSegment;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapQueryPartitionIterator<K, V, R>
extends AbstractMapQueryPartitionIterator<K, V, R> {
    private final MapProxyImpl<K, V> mapProxy;

    public MapQueryPartitionIterator(MapProxyImpl<K, V> mapProxy, int fetchSize, int partitionId, Predicate<K, V> predicate, Projection<Map.Entry<K, V>, R> projection) {
        super(mapProxy, fetchSize, partitionId, predicate, projection);
        this.mapProxy = mapProxy;
        this.advance();
    }

    @Override
    protected List<Data> fetch() {
        MapOperation op = this.mapProxy.getOperationProvider().createFetchWithQueryOperation(this.mapProxy.getName(), this.lastTableIndex, this.fetchSize, this.query);
        ResultSegment segment = this.invoke(op);
        QueryResult queryResult = (QueryResult)segment.getResult();
        ArrayList<Data> serialized = new ArrayList<Data>(queryResult.size());
        for (QueryResultRow row : queryResult) {
            serialized.add(row.getValue());
        }
        this.setLastTableIndex(serialized, segment.getNextTableIndexToReadFrom());
        return serialized;
    }

    private ResultSegment invoke(Operation operation) {
        InternalCompletableFuture future = this.mapProxy.getOperationService().invokeOnPartition(this.mapProxy.getServiceName(), operation, this.partitionId);
        return (ResultSegment)future.join();
    }

    @Override
    protected SerializationService getSerializationService() {
        return this.mapProxy.getNodeEngine().getSerializationService();
    }
}

