/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache;

import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.properties.HazelcastProperty;

public interface NearCache<K, V>
extends InitializingObject {
    public static final int DEFAULT_EXPIRATION_TASK_INITIAL_DELAY_SECONDS = 5;
    public static final int DEFAULT_EXPIRATION_TASK_PERIOD_SECONDS = 5;
    public static final String PROP_EXPIRATION_TASK_INITIAL_DELAY_SECONDS = "hazelcast.internal.nearcache.expiration.task.initial.delay.seconds";
    public static final String PROP_EXPIRATION_TASK_PERIOD_SECONDS = "hazelcast.internal.nearcache.expiration.task.period.seconds";
    public static final HazelcastProperty TASK_INITIAL_DELAY_SECONDS = new HazelcastProperty("hazelcast.internal.nearcache.expiration.task.initial.delay.seconds", 5);
    public static final HazelcastProperty TASK_PERIOD_SECONDS = new HazelcastProperty("hazelcast.internal.nearcache.expiration.task.period.seconds", 5);
    public static final Object CACHED_AS_NULL = new Object();
    public static final Object NOT_CACHED = new Object();

    public String getName();

    public V get(K var1);

    public void put(K var1, Data var2, V var3, Data var4);

    public void invalidate(K var1);

    public void clear();

    public void destroy();

    public int size();

    public NearCacheStats getNearCacheStats();

    public boolean isSerializeKeys();

    public void preload(DataStructureAdapter<Object, ?> var1);

    public void storeKeys();

    public boolean isPreloadDone();

    public <T> T unwrap(Class<T> var1);

    public long tryReserveForUpdate(K var1, Data var2);

    public V tryPublishReserved(K var1, V var2, long var3, boolean var5);
}

