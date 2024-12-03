/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl;

import com.hazelcast.internal.usercodedeployment.impl.ClassSource;
import java.util.HashMap;
import java.util.Map;

public final class ThreadLocalClassCache {
    public static final ThreadLocal<ThreadLocalClassCache> THREAD_LOCAL_CLASS_CACHE = new ThreadLocal();
    private int counter = 1;
    private Map<String, ClassSource> map = new HashMap<String, ClassSource>();

    private ThreadLocalClassCache() {
    }

    private int decCounter() {
        --this.counter;
        return this.counter;
    }

    private void incCounter() {
        ++this.counter;
    }

    public static void onStartDeserialization() {
        ThreadLocalClassCache threadLocalClassCache = THREAD_LOCAL_CLASS_CACHE.get();
        if (threadLocalClassCache != null) {
            threadLocalClassCache.incCounter();
        }
    }

    public static void onFinishDeserialization() {
        ThreadLocalClassCache threadLocalClassCache = THREAD_LOCAL_CLASS_CACHE.get();
        if (threadLocalClassCache != null && threadLocalClassCache.decCounter() == 0) {
            THREAD_LOCAL_CLASS_CACHE.remove();
        }
    }

    public static void store(String name, ClassSource classSource) {
        ThreadLocalClassCache threadLocalClassCache = THREAD_LOCAL_CLASS_CACHE.get();
        if (threadLocalClassCache == null) {
            threadLocalClassCache = new ThreadLocalClassCache();
            THREAD_LOCAL_CLASS_CACHE.set(threadLocalClassCache);
        }
        threadLocalClassCache.map.put(name, classSource);
    }

    public static ClassSource getFromCache(String name) {
        ThreadLocalClassCache threadLocalClassCache = THREAD_LOCAL_CLASS_CACHE.get();
        if (threadLocalClassCache != null) {
            return threadLocalClassCache.map.get(name);
        }
        return null;
    }
}

