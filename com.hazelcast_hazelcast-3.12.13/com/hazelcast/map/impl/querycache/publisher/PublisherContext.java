/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.core.IFunction;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.publisher.MapListenerRegistry;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.spi.NodeEngine;

public interface PublisherContext {
    public AccumulatorInfoSupplier getAccumulatorInfoSupplier();

    public MapPublisherRegistry getMapPublisherRegistry();

    public MapListenerRegistry getMapListenerRegistry();

    public IFunction<String, String> getListenerRegistrator();

    public QueryCacheContext getContext();

    public NodeEngine getNodeEngine();

    public void handleDisconnectedSubscriber(String var1);

    public void handleConnectedSubscriber(String var1);

    public void flush();
}

