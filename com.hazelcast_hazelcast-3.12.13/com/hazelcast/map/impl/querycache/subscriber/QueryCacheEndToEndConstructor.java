/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.util.ConstructorFunction;

public interface QueryCacheEndToEndConstructor
extends ConstructorFunction<String, InternalQueryCache> {
    public void createSubscriberAccumulator(AccumulatorInfo var1) throws Exception;

    public void createPublisherAccumulator(AccumulatorInfo var1) throws Exception;
}

