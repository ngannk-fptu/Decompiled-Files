/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.impl;

import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DistributedServiceWanEventCounters {
    private static final ConstructorFunction<String, DistributedObjectWanEventCounters> EVENT_COUNTER_CONSTRUCTOR_FN = new ConstructorFunction<String, DistributedObjectWanEventCounters>(){

        @Override
        public DistributedObjectWanEventCounters createNew(String ignored) {
            return new DistributedObjectWanEventCounters();
        }
    };
    private final ConcurrentHashMap<String, DistributedObjectWanEventCounters> eventCounterMap = new ConcurrentHashMap();

    public void incrementSync(String distributedObjectName) {
        this.incrementSync(distributedObjectName, 1);
    }

    public void incrementSync(String distributedObjectName, int count) {
        ConcurrencyUtil.getOrPutIfAbsent(this.eventCounterMap, distributedObjectName, DistributedServiceWanEventCounters.EVENT_COUNTER_CONSTRUCTOR_FN).incrementSyncCount(count);
    }

    public void incrementUpdate(String distributedObjectName) {
        ConcurrencyUtil.getOrPutIfAbsent(this.eventCounterMap, distributedObjectName, DistributedServiceWanEventCounters.EVENT_COUNTER_CONSTRUCTOR_FN).incrementUpdateCount();
    }

    public void incrementRemove(String distributedObjectName) {
        ConcurrencyUtil.getOrPutIfAbsent(this.eventCounterMap, distributedObjectName, DistributedServiceWanEventCounters.EVENT_COUNTER_CONSTRUCTOR_FN).incrementRemoveCount();
    }

    public void incrementDropped(String distributedObjectName) {
        ConcurrencyUtil.getOrPutIfAbsent(this.eventCounterMap, distributedObjectName, DistributedServiceWanEventCounters.EVENT_COUNTER_CONSTRUCTOR_FN).incrementDroppedCount();
    }

    public void removeCounter(String dataStructureName) {
        this.eventCounterMap.remove(dataStructureName);
    }

    public ConcurrentHashMap<String, DistributedObjectWanEventCounters> getEventCounterMap() {
        return this.eventCounterMap;
    }

    public static final class DistributedObjectWanEventCounters {
        private final AtomicLong syncCount = new AtomicLong();
        private final AtomicLong updateCount = new AtomicLong();
        private final AtomicLong removeCount = new AtomicLong();
        private final AtomicLong droppedCount = new AtomicLong();

        private DistributedObjectWanEventCounters() {
        }

        private void incrementSyncCount(int count) {
            this.syncCount.addAndGet(count);
        }

        private void incrementUpdateCount() {
            this.updateCount.incrementAndGet();
        }

        private void incrementRemoveCount() {
            this.removeCount.incrementAndGet();
        }

        private void incrementDroppedCount() {
            this.droppedCount.incrementAndGet();
        }

        public long getDroppedCount() {
            return this.droppedCount.longValue();
        }

        public long getSyncCount() {
            return this.syncCount.longValue();
        }

        public long getUpdateCount() {
            return this.updateCount.longValue();
        }

        public long getRemoveCount() {
            return this.removeCount.longValue();
        }
    }
}

