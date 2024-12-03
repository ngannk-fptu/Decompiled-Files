/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.configuration.Factory
 *  javax.cache.event.CacheEntryCreatedListener
 *  javax.cache.event.CacheEntryEvent
 *  javax.cache.event.CacheEntryEventFilter
 *  javax.cache.event.CacheEntryExpiredListener
 *  javax.cache.event.CacheEntryListener
 *  javax.cache.event.CacheEntryRemovedListener
 *  javax.cache.event.CacheEntryUpdatedListener
 *  javax.cache.event.EventType
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.ICache;
import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheEntryEventImpl;
import com.hazelcast.cache.impl.CacheEntryListenerProvider;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.cache.impl.CacheEventListener;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.CacheSyncListenerCompleter;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.ListenerWrapperEventFilter;
import com.hazelcast.spi.NotifiableEventListener;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.event.EventType;

public class CacheEventListenerAdaptor<K, V>
implements CacheEventListener,
CacheEntryListenerProvider<K, V>,
NotifiableEventListener<CacheService>,
ListenerWrapperEventFilter,
IdentifiedDataSerializable {
    private transient CacheEntryListener<K, V> cacheEntryListener;
    private transient CacheEntryCreatedListener cacheEntryCreatedListener;
    private transient CacheEntryRemovedListener cacheEntryRemovedListener;
    private transient CacheEntryUpdatedListener cacheEntryUpdatedListener;
    private transient CacheEntryExpiredListener cacheEntryExpiredListener;
    private transient CacheEntryEventFilter<? super K, ? super V> filter;
    private boolean isOldValueRequired;
    private transient SerializationService serializationService;
    private transient ICache<K, V> source;

    public CacheEventListenerAdaptor() {
    }

    public CacheEventListenerAdaptor(ICache<K, V> source, CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration, SerializationService serializationService) {
        this.source = source;
        this.serializationService = serializationService;
        this.cacheEntryListener = this.createCacheEntryListener(cacheEntryListenerConfiguration);
        this.cacheEntryCreatedListener = this.cacheEntryListener instanceof CacheEntryCreatedListener ? (CacheEntryCreatedListener)this.cacheEntryListener : null;
        this.cacheEntryRemovedListener = this.cacheEntryListener instanceof CacheEntryRemovedListener ? (CacheEntryRemovedListener)this.cacheEntryListener : null;
        this.cacheEntryUpdatedListener = this.cacheEntryListener instanceof CacheEntryUpdatedListener ? (CacheEntryUpdatedListener)this.cacheEntryListener : null;
        this.cacheEntryExpiredListener = this.cacheEntryListener instanceof CacheEntryExpiredListener ? (CacheEntryExpiredListener)this.cacheEntryListener : null;
        this.injectDependencies(this.cacheEntryListener);
        Factory filterFactory = cacheEntryListenerConfiguration.getCacheEntryEventFilterFactory();
        this.filter = filterFactory != null ? (CacheEntryEventFilter)filterFactory.create() : null;
        this.injectDependencies(this.filter);
        this.isOldValueRequired = cacheEntryListenerConfiguration.isOldValueRequired();
    }

    private CacheEntryListener<K, V> createCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        Factory cacheEntryListenerFactory = cacheEntryListenerConfiguration.getCacheEntryListenerFactory();
        this.injectDependencies(cacheEntryListenerFactory);
        return (CacheEntryListener)cacheEntryListenerFactory.create();
    }

    private void injectDependencies(Object obj) {
        ManagedContext managedContext = this.serializationService.getManagedContext();
        managedContext.initialize(obj);
    }

    @Override
    public CacheEntryListener<K, V> getCacheEntryListener() {
        return this.cacheEntryListener;
    }

    @Override
    public void handleEvent(Object eventObject) {
        if (eventObject instanceof CacheEventSet) {
            CacheEventSet cacheEventSet = (CacheEventSet)eventObject;
            try {
                if (cacheEventSet.getEventType() != CacheEventType.COMPLETED) {
                    this.handleEvent(cacheEventSet.getEventType().getType(), cacheEventSet.getEvents());
                }
            }
            finally {
                ((CacheSyncListenerCompleter)((Object)this.source)).countDownCompletionLatch(cacheEventSet.getCompletionId());
            }
        }
    }

    private void handleEvent(int type, Collection<CacheEventData> keys) {
        Iterable<CacheEntryEvent<K, V>> cacheEntryEvent = this.createCacheEntryEvent(keys);
        CacheEventType eventType = CacheEventType.getByType(type);
        switch (eventType) {
            case CREATED: {
                if (this.cacheEntryCreatedListener == null) break;
                this.cacheEntryCreatedListener.onCreated(cacheEntryEvent);
                break;
            }
            case UPDATED: {
                if (this.cacheEntryUpdatedListener == null) break;
                this.cacheEntryUpdatedListener.onUpdated(cacheEntryEvent);
                break;
            }
            case REMOVED: {
                if (this.cacheEntryRemovedListener == null) break;
                this.cacheEntryRemovedListener.onRemoved(cacheEntryEvent);
                break;
            }
            case EXPIRED: {
                if (this.cacheEntryExpiredListener == null) break;
                this.cacheEntryExpiredListener.onExpired(cacheEntryEvent);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid event type: " + eventType.name());
            }
        }
    }

    private Iterable<CacheEntryEvent<? extends K, ? extends V>> createCacheEntryEvent(Collection<CacheEventData> keys) {
        HashSet<CacheEntryEvent<K, V>> evt = new HashSet<CacheEntryEvent<K, V>>();
        for (CacheEventData cacheEventData : keys) {
            Object oldValue;
            Object newValue;
            boolean hasNewValue;
            EventType eventType = CacheEventType.convertToEventType(cacheEventData.getCacheEventType());
            Object key = this.toObject(cacheEventData.getDataKey());
            boolean bl = hasNewValue = eventType != EventType.REMOVED && eventType != EventType.EXPIRED;
            if (this.isOldValueRequired) {
                if (hasNewValue) {
                    newValue = this.toObject(cacheEventData.getDataValue());
                    oldValue = this.toObject(cacheEventData.getDataOldValue());
                } else {
                    newValue = oldValue = (Object)this.toObject(cacheEventData.getDataValue());
                }
            } else if (hasNewValue) {
                newValue = this.toObject(cacheEventData.getDataValue());
                oldValue = null;
            } else {
                newValue = null;
                oldValue = null;
            }
            CacheEntryEventImpl<K, Object> event = new CacheEntryEventImpl<K, Object>(this.source, eventType, key, newValue, oldValue);
            if (this.filter != null && !this.filter.evaluate(event)) continue;
            evt.add(event);
        }
        return evt;
    }

    private <T> T toObject(Data data) {
        return this.serializationService.toObject(data);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handle(int type, Collection<CacheEventData> keys, int completionId) {
        try {
            if (CacheEventType.getByType(type) != CacheEventType.COMPLETED) {
                this.handleEvent(type, keys);
            }
        }
        finally {
            ((CacheSyncListenerCompleter)((Object)this.source)).countDownCompletionLatch(completionId);
        }
    }

    @Override
    public void onRegister(CacheService cacheService, String serviceName, String topic, EventRegistration registration) {
        CacheContext cacheContext = cacheService.getOrCreateCacheContext(topic);
        cacheContext.increaseCacheEntryListenerCount();
    }

    @Override
    public void onDeregister(CacheService cacheService, String serviceName, String topic, EventRegistration registration) {
        CacheContext cacheContext = cacheService.getOrCreateCacheContext(topic);
        cacheContext.decreaseCacheEntryListenerCount();
    }

    @Override
    public boolean eval(Object event) {
        return true;
    }

    @Override
    public Object getListener() {
        return this;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 55;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
    }
}

