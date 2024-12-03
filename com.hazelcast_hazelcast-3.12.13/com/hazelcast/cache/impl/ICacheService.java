/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheEventContext;
import com.hazelcast.cache.impl.CacheEventListener;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.CachePartitionSegment;
import com.hazelcast.cache.impl.CacheStatisticsImpl;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.PreJoinCacheConfig;
import com.hazelcast.cache.impl.event.CacheWanEventPublisher;
import com.hazelcast.cache.impl.journal.CacheEventJournal;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.internal.eviction.ExpirationManager;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.FragmentedMigrationAwareService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.RemoteService;
import java.util.Collection;

public interface ICacheService
extends ManagedService,
RemoteService,
FragmentedMigrationAwareService,
EventPublishingService<Object, CacheEventListener> {
    public static final String CACHE_SUPPORT_NOT_AVAILABLE_ERROR_MESSAGE = "There is no valid JCache API library at classpath. Please be sure that there is a JCache API library in your classpath and it is newer than `0.x` and `1.0.0-PFD` versions!";
    public static final String SERVICE_NAME = "hz:impl:cacheService";
    public static final int MAX_ADD_CACHE_CONFIG_RETRIES = 100;

    public ICacheRecordStore getOrCreateRecordStore(String var1, int var2);

    public ICacheRecordStore getRecordStore(String var1, int var2);

    public CachePartitionSegment getSegment(int var1);

    public CacheConfig putCacheConfigIfAbsent(CacheConfig var1);

    public CacheConfig getCacheConfig(String var1);

    public CacheConfig findCacheConfig(String var1);

    public Collection<CacheConfig> getCacheConfigs();

    public CacheConfig deleteCacheConfig(String var1);

    public CachePartitionSegment[] getPartitionSegments();

    public CacheStatisticsImpl createCacheStatIfAbsent(String var1);

    public CacheContext getOrCreateCacheContext(String var1);

    public void deleteCache(String var1, String var2, boolean var3);

    public void deleteCacheStat(String var1);

    public void setStatisticsEnabled(CacheConfig var1, String var2, boolean var3);

    public void setManagementEnabled(CacheConfig var1, String var2, boolean var3);

    public void publishEvent(CacheEventContext var1);

    public void publishEvent(String var1, CacheEventSet var2, int var3);

    public NodeEngine getNodeEngine();

    public String registerListener(String var1, CacheEventListener var2, boolean var3);

    public String registerListener(String var1, CacheEventListener var2, EventFilter var3, boolean var4);

    public boolean deregisterListener(String var1, String var2);

    public void deregisterAllListener(String var1);

    public CacheStatistics getStatistics(String var1);

    public ExpirationManager getExpirationManager();

    public CacheOperationProvider getCacheOperationProvider(String var1, InMemoryFormat var2);

    public String addInvalidationListener(String var1, CacheEventListener var2, boolean var3);

    public void sendInvalidationEvent(String var1, Data var2, String var3);

    public boolean isWanReplicationEnabled(String var1);

    public CacheWanEventPublisher getCacheWanEventPublisher();

    public CacheEventJournal getEventJournal();

    public <K, V> void createCacheConfigOnAllMembers(PreJoinCacheConfig<K, V> var1);

    public <K, V> void setTenantControl(CacheConfig<K, V> var1);
}

