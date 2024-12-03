/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.impl;

import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;
import java.util.concurrent.ConcurrentHashMap;

public class WanEventCounters {
    private static final ConstructorFunction<String, WanPublisherEventCounters> WAN_EVENT_COUNTER_CONSTRUCTOR_FN = new ConstructorFunction<String, WanPublisherEventCounters>(){

        @Override
        public WanPublisherEventCounters createNew(String ignored) {
            return new WanPublisherEventCounters();
        }
    };
    private final ConcurrentHashMap<String, WanPublisherEventCounters> eventCounterMap = new ConcurrentHashMap();

    public DistributedServiceWanEventCounters getWanEventCounter(String wanReplicationName, String targetGroupName, String serviceName) {
        String wanPublisherId = wanReplicationName + ":" + targetGroupName;
        WanPublisherEventCounters serviceWanEventCounters = ConcurrencyUtil.getOrPutIfAbsent(this.eventCounterMap, wanPublisherId, WAN_EVENT_COUNTER_CONSTRUCTOR_FN);
        return serviceWanEventCounters.getWanEventCounter(serviceName);
    }

    public void removeCounter(String serviceName, String dataStructureName) {
        for (WanPublisherEventCounters publisherWanCounterContainer : this.eventCounterMap.values()) {
            publisherWanCounterContainer.removeCounter(serviceName, dataStructureName);
        }
    }

    private static final class WanPublisherEventCounters {
        private final DistributedServiceWanEventCounters mapEventCounters = new DistributedServiceWanEventCounters();
        private final DistributedServiceWanEventCounters cacheEventCounters = new DistributedServiceWanEventCounters();

        private WanPublisherEventCounters() {
        }

        void removeCounter(String serviceName, String dataStructureName) {
            this.getWanEventCounter(serviceName).removeCounter(dataStructureName);
        }

        DistributedServiceWanEventCounters getWanEventCounter(String serviceName) {
            if ("hz:impl:mapService".equals(serviceName)) {
                return this.mapEventCounters;
            }
            if ("hz:impl:cacheService".equals(serviceName)) {
                return this.cacheEventCounters;
            }
            throw new IllegalArgumentException("Unsupported service for counting WAN events " + serviceName);
        }
    }
}

