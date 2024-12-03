/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheClearResponse;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.Map;
import java.util.Set;

public final class CacheProxyUtil {
    public static final int AWAIT_COMPLETION_TIMEOUT_SECONDS = 60;
    public static final String NULL_KEY_IS_NOT_ALLOWED = "Null key is not allowed!";
    private static final String NULL_VALUE_IS_NOT_ALLOWED = "Null value is not allowed!";
    private static final String NULL_SET_IS_NOT_ALLOWED = "Null set is not allowed!";

    private CacheProxyUtil() {
    }

    public static void validateResults(Map<Integer, Object> results) {
        for (Object result : results.values()) {
            Object response;
            if (result == null || !(result instanceof CacheClearResponse) || !((response = ((CacheClearResponse)result).getResponse()) instanceof Throwable)) continue;
            ExceptionUtil.sneakyThrow((Throwable)response);
        }
    }

    public static int getPartitionId(NodeEngine nodeEngine, Data key) {
        return nodeEngine.getPartitionService().getPartitionId(key);
    }

    public static <K> void validateNotNull(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
    }

    public static <K, V> void validateNotNull(K key, V value) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        Preconditions.checkNotNull(value, NULL_VALUE_IS_NOT_ALLOWED);
    }

    public static <K, V> void validateNotNull(K key, V value1, V value2) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        Preconditions.checkNotNull(value1, NULL_VALUE_IS_NOT_ALLOWED);
        Preconditions.checkNotNull(value2, NULL_VALUE_IS_NOT_ALLOWED);
    }

    public static <K> void validateNotNull(Set<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException(NULL_SET_IS_NOT_ALLOWED);
        }
    }

    public static <K, V> void validateNotNull(Map<? extends K, ? extends V> map) {
        Preconditions.checkNotNull(map, "map is null");
        boolean containsNullKey = false;
        boolean containsNullValue = false;
        try {
            containsNullKey = map.containsKey(null);
        }
        catch (NullPointerException e) {
            EmptyStatement.ignore(e);
        }
        try {
            containsNullValue = map.containsValue(null);
        }
        catch (NullPointerException e) {
            EmptyStatement.ignore(e);
        }
        if (containsNullKey) {
            throw new NullPointerException(NULL_KEY_IS_NOT_ALLOWED);
        }
        if (containsNullValue) {
            throw new NullPointerException(NULL_VALUE_IS_NOT_ALLOWED);
        }
    }

    public static <K> void validateConfiguredTypes(CacheConfig cacheConfig, K key) throws ClassCastException {
        Class keyType = cacheConfig.getKeyType();
        CacheProxyUtil.validateConfiguredKeyType(keyType, key);
    }

    public static <K, V> void validateConfiguredTypes(CacheConfig cacheConfig, K key, V value) throws ClassCastException {
        Class keyType = cacheConfig.getKeyType();
        Class valueType = cacheConfig.getValueType();
        CacheProxyUtil.validateConfiguredKeyType(keyType, key);
        CacheProxyUtil.validateConfiguredValueType(valueType, value);
    }

    public static <K, V> void validateConfiguredTypes(CacheConfig cacheConfig, K key, V value1, V value2) throws ClassCastException {
        Class keyType = cacheConfig.getKeyType();
        Class valueType = cacheConfig.getValueType();
        CacheProxyUtil.validateConfiguredKeyType(keyType, key);
        CacheProxyUtil.validateConfiguredValueType(valueType, value1);
        CacheProxyUtil.validateConfiguredValueType(valueType, value2);
    }

    public static <K> void validateConfiguredKeyType(Class<K> keyType, K key) throws ClassCastException {
        if (Object.class != keyType && !keyType.isAssignableFrom(key.getClass())) {
            throw new ClassCastException("Key '" + key + "' is not assignable to " + keyType);
        }
    }

    public static <V> void validateConfiguredValueType(Class<V> valueType, V value) throws ClassCastException {
        if (Object.class != valueType && !valueType.isAssignableFrom(value.getClass())) {
            throw new ClassCastException("Value '" + value + "' is not assignable to " + valueType);
        }
    }
}

