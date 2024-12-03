/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.querycache.ListenerRegistrationHelper;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.publisher.MapListenerRegistry;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.QueryCacheListenerRegistry;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.EventService;
import com.hazelcast.util.ExceptionUtil;
import java.io.IOException;

public class DestroyQueryCacheOperation
extends MapOperation {
    private String cacheId;
    private transient boolean result;

    public DestroyQueryCacheOperation() {
    }

    public DestroyQueryCacheOperation(String mapName, String cacheId) {
        super(mapName);
        this.cacheId = cacheId;
    }

    @Override
    public void run() throws Exception {
        try {
            this.deregisterLocalIMapListener();
            this.removeAccumulatorInfo();
            this.removePublisherAccumulators();
            this.removeAllListeners();
            this.result = true;
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.cacheId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.cacheId = in.readUTF();
    }

    private void deregisterLocalIMapListener() {
        PublisherContext publisherContext = this.getPublisherContext();
        MapListenerRegistry registry = publisherContext.getMapListenerRegistry();
        QueryCacheListenerRegistry listenerRegistry = registry.getOrNull(this.name);
        if (listenerRegistry == null) {
            return;
        }
        String listenerId = listenerRegistry.remove(this.cacheId);
        this.mapService.getMapServiceContext().removeEventListener(this.name, listenerId);
    }

    private void removeAccumulatorInfo() {
        PublisherContext publisherContext = this.getPublisherContext();
        AccumulatorInfoSupplier infoSupplier = publisherContext.getAccumulatorInfoSupplier();
        infoSupplier.remove(this.name, this.cacheId);
    }

    private void removePublisherAccumulators() {
        PublisherContext publisherContext = this.getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        PublisherRegistry publisherRegistry = mapPublisherRegistry.getOrNull(this.name);
        if (publisherRegistry == null) {
            return;
        }
        publisherRegistry.remove(this.cacheId);
    }

    private void removeAllListeners() {
        EventService eventService = this.getNodeEngine().getEventService();
        eventService.deregisterAllListeners("hz:impl:mapService", ListenerRegistrationHelper.generateListenerName(this.name, this.cacheId));
    }

    private PublisherContext getPublisherContext() {
        QueryCacheContext queryCacheContext = this.mapServiceContext.getQueryCacheContext();
        return queryCacheContext.getPublisherContext();
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 123;
    }
}

