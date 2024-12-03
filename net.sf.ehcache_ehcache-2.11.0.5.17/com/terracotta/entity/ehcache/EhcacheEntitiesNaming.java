/*
 * Decompiled with CFR 0.152.
 */
package com.terracotta.entity.ehcache;

public final class EhcacheEntitiesNaming {
    private static final String DELIMITER = "|";
    private static final String EHCACHE_NAME_PREFIX = "__tc_clustered-ehcache";
    private static final String CACHE_MANAGER_ENTITY_LOCK_PREFIX = "__entity_cache_manager_lock@";
    private static final String ASYNC_CONFIG_MAP_NAME = "asyncConfigMap";
    private static final String CACHE_MANAGER_CONFIG_MAP_PREFIX = "__entity_cache_manager_config@";
    private static final String CLUSTERED_STORE_CONFIG_MAP = "__tc_clustered-ehcache|configMap";

    public static String getToolkitCacheNameFor(String cacheMgrName, String cacheName) {
        return "__tc_clustered-ehcache|" + cacheMgrName + DELIMITER + cacheName;
    }

    public static String getToolkitCacheConfigMapName(String fullQualifiedCacheName) {
        return fullQualifiedCacheName + DELIMITER + CLUSTERED_STORE_CONFIG_MAP;
    }

    public static String getCacheManagerLockNameFor(String cacheManagerName) {
        return CACHE_MANAGER_ENTITY_LOCK_PREFIX + cacheManagerName;
    }

    public static String getAsyncNameFor(String cacheMgrName, String cacheName) {
        return cacheMgrName + DELIMITER + cacheName;
    }

    public static String getCacheManagerConfigMapName(String cacheMgrName) {
        return CACHE_MANAGER_CONFIG_MAP_PREFIX + cacheMgrName;
    }

    public static String getAsyncConfigMapName() {
        return ASYNC_CONFIG_MAP_NAME;
    }

    private EhcacheEntitiesNaming() {
    }
}

