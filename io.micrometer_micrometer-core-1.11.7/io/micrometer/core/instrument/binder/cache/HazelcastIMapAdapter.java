/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.binder.cache;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;

class HazelcastIMapAdapter {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(HazelcastIMapAdapter.class);
    private static final Class<?> CLASS_I_MAP = HazelcastIMapAdapter.resolveOneOf("com.hazelcast.map.IMap", "com.hazelcast.core.IMap");
    private static final Class<?> CLASS_LOCAL_MAP = HazelcastIMapAdapter.resolveOneOf("com.hazelcast.map.LocalMapStats", "com.hazelcast.monitor.LocalMapStats");
    private static final Class<?> CLASS_NEAR_CACHE_STATS = HazelcastIMapAdapter.resolveOneOf("com.hazelcast.nearcache.NearCacheStats", "com.hazelcast.monitor.NearCacheStats");
    private static final MethodHandle GET_NAME = HazelcastIMapAdapter.resolveIMapMethod("getName", MethodType.methodType(String.class));
    private static final MethodHandle GET_LOCAL_MAP_STATS = HazelcastIMapAdapter.resolveIMapMethod("getLocalMapStats", MethodType.methodType(CLASS_LOCAL_MAP));
    private final WeakReference<Object> cache;

    HazelcastIMapAdapter(Object cache) {
        this.cache = new WeakReference<Object>(cache);
    }

    static String nameOf(Object cache) {
        try {
            return GET_NAME.invoke(cache);
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Nullable
    LocalMapStats getLocalMapStats() {
        Object ref = this.cache.get();
        if (ref == null) {
            return null;
        }
        Object result = HazelcastIMapAdapter.invoke(GET_LOCAL_MAP_STATS, ref);
        return result == null ? null : new LocalMapStats(result);
    }

    private static MethodHandle resolveIMapMethod(String name, MethodType mt) {
        try {
            return MethodHandles.publicLookup().findVirtual(CLASS_I_MAP, name, mt);
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Class<?> resolveOneOf(String class1, String class2) {
        try {
            return Class.forName(class1);
        }
        catch (ClassNotFoundException e) {
            try {
                return Class.forName(class2);
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    private static Object invoke(MethodHandle mh, Object object) {
        try {
            return mh.invoke(object);
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    static class LocalMapStats {
        private static final MethodHandle GET_NEAR_CACHE_STATS = LocalMapStats.resolveMethod("getNearCacheStats", MethodType.methodType(HazelcastIMapAdapter.access$000()));
        private static final MethodHandle GET_OWNED_ENTRY_COUNT = LocalMapStats.resolveMethod("getOwnedEntryCount", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_HITS = LocalMapStats.resolveMethod("getHits", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_PUT_OPERATION_COUNT = LocalMapStats.resolveMethod("getPutOperationCount", MethodType.methodType(Long.TYPE));
        @Nullable
        private static final MethodHandle GET_SET_OPERATION_COUNT = LocalMapStats.resolveMethod("getSetOperationCount", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_BACKUP_ENTRY_COUNT = LocalMapStats.resolveMethod("getBackupEntryCount", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_BACKUP_ENTRY_MEMORY_COST = LocalMapStats.resolveMethod("getBackupEntryMemoryCost", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_OWNED_ENTRY_MEMORY_COST = LocalMapStats.resolveMethod("getOwnedEntryMemoryCost", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_GET_OPERATION_COUNT = LocalMapStats.resolveMethod("getGetOperationCount", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_TOTAL_GET_LATENCY = LocalMapStats.resolveMethod("getTotalGetLatency", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_TOTAL_PUT_LATENCY = LocalMapStats.resolveMethod("getTotalPutLatency", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_REMOVE_OPERATION_COUNT = LocalMapStats.resolveMethod("getRemoveOperationCount", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_TOTAL_REMOVE_LATENCY = LocalMapStats.resolveMethod("getTotalRemoveLatency", MethodType.methodType(Long.TYPE));
        private final Object localMapStats;

        LocalMapStats(Object localMapStats) {
            this.localMapStats = localMapStats;
        }

        long getOwnedEntryCount() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_OWNED_ENTRY_COUNT, this.localMapStats);
        }

        long getHits() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_HITS, this.localMapStats);
        }

        long getPutOperationCount() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_PUT_OPERATION_COUNT, this.localMapStats);
        }

        long getSetOperationCount() {
            if (GET_SET_OPERATION_COUNT == null) {
                return 0L;
            }
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_SET_OPERATION_COUNT, this.localMapStats);
        }

        double getBackupEntryCount() {
            return ((Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_BACKUP_ENTRY_COUNT, this.localMapStats)).longValue();
        }

        long getBackupEntryMemoryCost() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_BACKUP_ENTRY_MEMORY_COST, this.localMapStats);
        }

        long getOwnedEntryMemoryCost() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_OWNED_ENTRY_MEMORY_COST, this.localMapStats);
        }

        long getGetOperationCount() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_GET_OPERATION_COUNT, this.localMapStats);
        }

        NearCacheStats getNearCacheStats() {
            Object result = HazelcastIMapAdapter.invoke(LocalMapStats.GET_NEAR_CACHE_STATS, this.localMapStats);
            return result == null ? null : new NearCacheStats(result);
        }

        long getTotalGetLatency() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_TOTAL_GET_LATENCY, this.localMapStats);
        }

        long getTotalPutLatency() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_TOTAL_PUT_LATENCY, this.localMapStats);
        }

        long getRemoveOperationCount() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_REMOVE_OPERATION_COUNT, this.localMapStats);
        }

        long getTotalRemoveLatency() {
            return (Long)HazelcastIMapAdapter.invoke(LocalMapStats.GET_TOTAL_REMOVE_LATENCY, this.localMapStats);
        }

        @Nullable
        private static MethodHandle resolveMethod(String name, MethodType mt) {
            try {
                return MethodHandles.publicLookup().findVirtual(CLASS_LOCAL_MAP, name, mt);
            }
            catch (IllegalAccessException | NoSuchMethodException e) {
                log.debug("Failed to resolve method: " + name, (Throwable)e);
                return null;
            }
        }
    }

    static class NearCacheStats {
        private static final MethodHandle GET_HITS = NearCacheStats.resolveMethod("getHits", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_MISSES = NearCacheStats.resolveMethod("getMisses", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_EVICTIONS = NearCacheStats.resolveMethod("getEvictions", MethodType.methodType(Long.TYPE));
        private static final MethodHandle GET_PERSISTENCE_COUNT = NearCacheStats.resolveMethod("getPersistenceCount", MethodType.methodType(Long.TYPE));
        private Object nearCacheStats;

        NearCacheStats(Object nearCacheStats) {
            this.nearCacheStats = nearCacheStats;
        }

        long getHits() {
            return (Long)HazelcastIMapAdapter.invoke(NearCacheStats.GET_HITS, this.nearCacheStats);
        }

        long getMisses() {
            return (Long)HazelcastIMapAdapter.invoke(NearCacheStats.GET_MISSES, this.nearCacheStats);
        }

        long getEvictions() {
            return (Long)HazelcastIMapAdapter.invoke(NearCacheStats.GET_EVICTIONS, this.nearCacheStats);
        }

        long getPersistenceCount() {
            return (Long)HazelcastIMapAdapter.invoke(NearCacheStats.GET_PERSISTENCE_COUNT, this.nearCacheStats);
        }

        private static MethodHandle resolveMethod(String name, MethodType mt) {
            try {
                return MethodHandles.publicLookup().findVirtual(CLASS_NEAR_CACHE_STATS, name, mt);
            }
            catch (IllegalAccessException | NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

