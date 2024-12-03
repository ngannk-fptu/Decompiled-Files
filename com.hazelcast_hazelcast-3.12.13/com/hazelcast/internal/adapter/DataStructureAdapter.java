/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.integration.CompletionListener
 *  javax.cache.processor.EntryProcessor
 *  javax.cache.processor.EntryProcessorException
 *  javax.cache.processor.EntryProcessorResult
 */
package com.hazelcast.internal.adapter;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.adapter.DataStructureAdapterMethod;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.query.Predicate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public interface DataStructureAdapter<K, V> {
    public int size();

    public V get(K var1);

    public ICompletableFuture<V> getAsync(K var1);

    public void set(K var1, V var2);

    public ICompletableFuture<Void> setAsync(K var1, V var2);

    public ICompletableFuture<Void> setAsync(K var1, V var2, long var3, TimeUnit var5);

    public ICompletableFuture<Void> setAsync(K var1, V var2, ExpiryPolicy var3);

    public V put(K var1, V var2);

    public ICompletableFuture<V> putAsync(K var1, V var2);

    public ICompletableFuture<V> putAsync(K var1, V var2, long var3, TimeUnit var5);

    public ICompletableFuture<V> putAsync(K var1, V var2, ExpiryPolicy var3);

    public void putTransient(K var1, V var2, long var3, TimeUnit var5);

    public boolean putIfAbsent(K var1, V var2);

    public ICompletableFuture<Boolean> putIfAbsentAsync(K var1, V var2);

    public void setTtl(K var1, long var2, TimeUnit var4);

    public V replace(K var1, V var2);

    public boolean replace(K var1, V var2, V var3);

    public V remove(K var1);

    public boolean remove(K var1, V var2);

    public ICompletableFuture<V> removeAsync(K var1);

    public void delete(K var1);

    public ICompletableFuture<Boolean> deleteAsync(K var1);

    public boolean evict(K var1);

    public <T> T invoke(K var1, EntryProcessor<K, V, T> var2, Object ... var3) throws EntryProcessorException;

    public Object executeOnKey(K var1, com.hazelcast.map.EntryProcessor var2);

    public Map<K, Object> executeOnKeys(Set<K> var1, com.hazelcast.map.EntryProcessor var2);

    public Map<K, Object> executeOnEntries(com.hazelcast.map.EntryProcessor var1);

    public Map<K, Object> executeOnEntries(com.hazelcast.map.EntryProcessor var1, Predicate var2);

    public boolean containsKey(K var1);

    public void loadAll(boolean var1);

    public void loadAll(Set<K> var1, boolean var2);

    public void loadAll(Set<? extends K> var1, boolean var2, CompletionListener var3);

    public Map<K, V> getAll(Set<K> var1);

    public void putAll(Map<K, V> var1);

    public void removeAll();

    public void removeAll(Set<K> var1);

    public void evictAll();

    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> var1, EntryProcessor<K, V, T> var2, Object ... var3);

    public void clear();

    public void close();

    public void destroy();

    public void setExpiryPolicy(Set<K> var1, ExpiryPolicy var2);

    public boolean setExpiryPolicy(K var1, ExpiryPolicy var2);

    public LocalMapStats getLocalMapStats();

    public static enum DataStructureMethods implements DataStructureAdapterMethod
    {
        SIZE("size", new Class[0]),
        GET("get", Object.class),
        GET_ASYNC("getAsync", Object.class),
        SET("set", Object.class, Object.class),
        SET_ASYNC("setAsync", Object.class, Object.class),
        SET_ASYNC_WITH_TTL("setAsync", Object.class, Object.class, Long.TYPE, TimeUnit.class),
        SET_ASYNC_WITH_EXPIRY_POLICY("setAsync", Object.class, Object.class, ExpiryPolicy.class),
        PUT("put", Object.class, Object.class),
        PUT_ASYNC("putAsync", Object.class, Object.class),
        PUT_ASYNC_WITH_TTL("putAsync", Object.class, Object.class, Long.TYPE, TimeUnit.class),
        PUT_ASYNC_WITH_EXPIRY_POLICY("putAsync", Object.class, Object.class, ExpiryPolicy.class),
        PUT_TRANSIENT("putTransient", Object.class, Object.class, Long.TYPE, TimeUnit.class),
        PUT_IF_ABSENT("putIfAbsent", Object.class, Object.class),
        PUT_IF_ABSENT_ASYNC("putIfAbsentAsync", Object.class, Object.class),
        REPLACE("replace", Object.class, Object.class),
        REPLACE_WITH_OLD_VALUE("replace", Object.class, Object.class, Object.class),
        REMOVE("remove", Object.class),
        REMOVE_WITH_OLD_VALUE("remove", Object.class, Object.class),
        REMOVE_ASYNC("removeAsync", Object.class),
        DELETE("delete", Object.class),
        DELETE_ASYNC("deleteAsync", Object.class),
        EVICT("evict", Object.class),
        INVOKE("invoke", Object.class, EntryProcessor.class, Object[].class),
        EXECUTE_ON_KEY("executeOnKey", Object.class, com.hazelcast.map.EntryProcessor.class),
        EXECUTE_ON_KEYS("executeOnKeys", Set.class, com.hazelcast.map.EntryProcessor.class),
        EXECUTE_ON_ENTRIES("executeOnEntries", com.hazelcast.map.EntryProcessor.class),
        EXECUTE_ON_ENTRIES_WITH_PREDICATE("executeOnEntries", com.hazelcast.map.EntryProcessor.class, Predicate.class),
        CONTAINS_KEY("containsKey", Object.class),
        LOAD_ALL("loadAll", Boolean.TYPE),
        LOAD_ALL_WITH_KEYS("loadAll", Set.class, Boolean.TYPE),
        LOAD_ALL_WITH_LISTENER("loadAll", Set.class, Boolean.TYPE, CompletionListener.class),
        GET_ALL("getAll", Set.class),
        PUT_ALL("putAll", Map.class),
        REMOVE_ALL("removeAll", new Class[0]),
        REMOVE_ALL_WITH_KEYS("removeAll", Set.class),
        EVICT_ALL("evictAll", new Class[0]),
        INVOKE_ALL("invokeAll", Set.class, EntryProcessor.class, Object[].class),
        CLEAR("clear", new Class[0]),
        CLOSE("close", new Class[0]),
        DESTROY("destroy", new Class[0]),
        GET_LOCAL_MAP_STATS("getLocalMapStats", new Class[0]),
        SET_TTL("setTtl", Object.class, Long.TYPE, TimeUnit.class),
        SET_EXPIRY_POLICY_MULTI_KEY("setExpiryPolicy", Set.class, ExpiryPolicy.class),
        SET_EXPIRY_POLICY("setExpiryPolicy", Object.class, ExpiryPolicy.class);

        private final String methodName;
        private final Class<?>[] parameterTypes;

        private DataStructureMethods(String methodName, Class<?> ... parameterTypes) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        @Override
        public String getMethodName() {
            return this.methodName;
        }

        @Override
        @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
        public Class<?>[] getParameterTypes() {
            return this.parameterTypes;
        }

        @Override
        public String getParameterTypeString() {
            StringBuilder sb = new StringBuilder();
            String delimiter = "";
            for (Class<?> parameterType : this.parameterTypes) {
                sb.append(delimiter).append(parameterType.getSimpleName());
                delimiter = ", ";
            }
            return sb.toString();
        }
    }
}

