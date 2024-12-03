/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.map.impl.querycache.Registry;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PartitionAccumulatorRegistry
implements Registry<Integer, Accumulator> {
    private final EventFilter eventFilter;
    private final AccumulatorInfo info;
    private final ConcurrentMap<Integer, Accumulator> accumulators;
    private final ConstructorFunction<Integer, Accumulator> accumulatorConstructor;
    private volatile String uuid;

    public PartitionAccumulatorRegistry(AccumulatorInfo info, ConstructorFunction<Integer, Accumulator> accumulatorConstructor) {
        this.info = info;
        this.eventFilter = this.createEventFilter();
        this.accumulatorConstructor = accumulatorConstructor;
        this.accumulators = new ConcurrentHashMap<Integer, Accumulator>();
    }

    private EventFilter createEventFilter() {
        boolean includeValue = this.info.isIncludeValue();
        Predicate predicate = this.info.getPredicate();
        return new QueryEventFilter(includeValue, null, predicate);
    }

    @Override
    public Accumulator getOrCreate(Integer partitionId) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.accumulators, partitionId, this.accumulatorConstructor);
    }

    @Override
    public Accumulator getOrNull(Integer partitionId) {
        return (Accumulator)this.accumulators.get(partitionId);
    }

    @Override
    public Map<Integer, Accumulator> getAll() {
        return Collections.unmodifiableMap(this.accumulators);
    }

    @Override
    public Accumulator remove(Integer id) {
        return (Accumulator)this.accumulators.remove(id);
    }

    public EventFilter getEventFilter() {
        return this.eventFilter;
    }

    public AccumulatorInfo getInfo() {
        return this.info;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

