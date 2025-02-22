/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.iterator;

import com.hazelcast.map.impl.iterator.AbstractCursor;
import com.hazelcast.map.impl.iterator.AbstractMapPartitionIterator;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.List;

public class MapPartitionIterator<K, V>
extends AbstractMapPartitionIterator<K, V> {
    private final MapProxyImpl<K, V> mapProxy;

    public MapPartitionIterator(MapProxyImpl<K, V> mapProxy, int fetchSize, int partitionId, boolean prefetchValues) {
        super(mapProxy, fetchSize, partitionId, prefetchValues);
        this.mapProxy = mapProxy;
        this.advance();
    }

    @Override
    protected List fetch() {
        String name = this.mapProxy.getName();
        MapOperationProvider operationProvider = this.mapProxy.getOperationProvider();
        MapOperation operation = this.prefetchValues ? operationProvider.createFetchEntriesOperation(name, this.lastTableIndex, this.fetchSize) : operationProvider.createFetchKeysOperation(name, this.lastTableIndex, this.fetchSize);
        Object cursor = this.invoke(operation);
        this.setLastTableIndex(((AbstractCursor)cursor).getBatch(), ((AbstractCursor)cursor).getNextTableIndexToReadFrom());
        return ((AbstractCursor)cursor).getBatch();
    }

    private <T extends AbstractCursor> T invoke(Operation operation) {
        InternalCompletableFuture future = this.mapProxy.getOperationService().invokeOnPartition(this.mapProxy.getServiceName(), operation, this.partitionId);
        return (T)((AbstractCursor)future.join());
    }

    @Override
    protected SerializationService getSerializationService() {
        return this.mapProxy.getNodeEngine().getSerializationService();
    }
}

