/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.CacheEventContext;
import com.hazelcast.cache.impl.CacheEventDataImpl;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.internal.nearcache.impl.invalidation.BatchInvalidator;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationUtils;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidator;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.internal.nearcache.impl.invalidation.NonStopInvalidator;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.util.Collection;

public class CacheEventHandler {
    private final NodeEngine nodeEngine;
    private final Invalidator invalidator;

    CacheEventHandler(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.invalidator = this.createInvalidator();
    }

    private Invalidator createInvalidator() {
        boolean batchingEnabled;
        HazelcastProperties hazelcastProperties = this.nodeEngine.getProperties();
        int batchSize = hazelcastProperties.getInteger(GroupProperty.CACHE_INVALIDATION_MESSAGE_BATCH_SIZE);
        int batchFrequencySeconds = hazelcastProperties.getInteger(GroupProperty.CACHE_INVALIDATION_MESSAGE_BATCH_FREQUENCY_SECONDS);
        boolean bl = batchingEnabled = hazelcastProperties.getBoolean(GroupProperty.CACHE_INVALIDATION_MESSAGE_BATCH_ENABLED) && batchSize > 1;
        if (batchingEnabled) {
            return new BatchInvalidator("hz:impl:cacheService", batchSize, batchFrequencySeconds, InvalidationUtils.TRUE_FILTER, this.nodeEngine);
        }
        return new NonStopInvalidator("hz:impl:cacheService", InvalidationUtils.TRUE_FILTER, this.nodeEngine);
    }

    public MetaDataGenerator getMetaDataGenerator() {
        return this.invalidator.getMetaDataGenerator();
    }

    void publishEvent(CacheEventContext cacheEventContext) {
        IdentifiedDataSerializable eventData;
        String cacheName;
        EventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> candidates = eventService.getRegistrations("hz:impl:cacheService", cacheName = cacheEventContext.getCacheName());
        if (candidates.isEmpty()) {
            return;
        }
        CacheEventType eventType = cacheEventContext.getEventType();
        switch (eventType) {
            case CREATED: 
            case UPDATED: 
            case REMOVED: 
            case EXPIRED: {
                CacheEventDataImpl cacheEventData = new CacheEventDataImpl(cacheName, eventType, cacheEventContext.getDataKey(), cacheEventContext.getDataValue(), cacheEventContext.getDataOldValue(), cacheEventContext.isOldValueAvailable());
                CacheEventSet eventSet = new CacheEventSet(eventType, cacheEventContext.getCompletionId());
                eventSet.addEventData(cacheEventData);
                eventData = eventSet;
                break;
            }
            case EVICTED: 
            case INVALIDATED: {
                eventData = new CacheEventDataImpl(cacheName, eventType, cacheEventContext.getDataKey(), null, null, false);
                break;
            }
            case COMPLETED: {
                CacheEventDataImpl completedEventData = new CacheEventDataImpl(cacheName, eventType, cacheEventContext.getDataKey(), cacheEventContext.getDataValue(), null, false);
                CacheEventSet eventSet = new CacheEventSet(eventType, cacheEventContext.getCompletionId());
                eventSet.addEventData(completedEventData);
                eventData = eventSet;
                break;
            }
            default: {
                throw new IllegalArgumentException("Event Type not defined to create an eventData during publish: " + eventType.name());
            }
        }
        eventService.publishEvent("hz:impl:cacheService", candidates, (Object)eventData, cacheEventContext.getOrderKey());
    }

    void publishEvent(String cacheNameWithPrefix, CacheEventSet eventSet, int orderKey) {
        EventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> candidates = eventService.getRegistrations("hz:impl:cacheService", cacheNameWithPrefix);
        if (candidates.isEmpty()) {
            return;
        }
        eventService.publishEvent("hz:impl:cacheService", candidates, (Object)eventSet, orderKey);
    }

    void sendInvalidationEvent(String name, Data key, String sourceUuid) {
        if (key == null) {
            this.invalidator.invalidateAllKeys(name, sourceUuid);
        } else {
            this.invalidator.invalidateKey(key, name, sourceUuid);
        }
    }

    public void resetPartitionMetaData(String name, int partitionId) {
        this.invalidator.resetPartitionMetaData(name, partitionId);
    }

    public void destroy(String name, String sourceUuid) {
        this.invalidator.destroy(name, sourceUuid);
    }

    void shutdown() {
        this.invalidator.shutdown();
    }
}

