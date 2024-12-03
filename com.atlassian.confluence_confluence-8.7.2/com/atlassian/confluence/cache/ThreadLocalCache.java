/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalCache {
    private static final ThreadLocal<Map<Object, Object>> cache = new ThreadLocal();
    private static final Logger log = LoggerFactory.getLogger(ThreadLocalCache.class);

    public static void put(Object key, Object value) {
        Map<Object, Object> cacheMap = cache.get();
        if (cacheMap == null) {
            log.debug("ThreadLocalCache is not initialised. Could not insert ({}, {})", key, value);
            return;
        }
        cacheMap.put(key, value);
    }

    public static Object get(Object key) {
        Map<Object, Object> cacheMap = cache.get();
        if (cacheMap == null) {
            log.debug("ThreadLocalCache is not initialised. Could not retrieve value for key {}", key);
            return null;
        }
        return cacheMap.get(key);
    }

    public static void init() {
        if (cache.get() != null) {
            log.warn("ThreadLocalCache is already initialised. Ignoring reinitialisation attempt.");
            return;
        }
        cache.set(new HashMap());
    }

    public static boolean isInit() {
        return cache.get() != null;
    }

    public static void dispose() {
        cache.remove();
    }

    public static void flush() {
        Map<Object, Object> cacheMap = cache.get();
        if (cacheMap == null) {
            log.debug("ThreadLocalCache is not initialised. Ignoring attempt to flush it.");
            return;
        }
        cacheMap.clear();
    }
}

