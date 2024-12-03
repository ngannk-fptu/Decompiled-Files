/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.VCacheUtils
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.VCacheUtils;
import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.service.VCacheLock;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExternalCacheRequestContext<V> {
    private static final Logger log = LoggerFactory.getLogger(AbstractExternalCacheRequestContext.class);
    protected final String name;
    private final VCacheLock globalLock;
    private final ExternalCacheKeyGenerator keyGenerator;
    private final Supplier<String> partitionSupplier;
    private final BiMap<String, String> internalToExternalKeyMap = Maps.synchronizedBiMap((BiMap)HashBiMap.create());
    private final Map<String, String> externalToInternalKeyMap = Collections.unmodifiableMap(this.internalToExternalKeyMap.inverse());
    private final Map<String, Optional<V>> internalKeyToValueMap = new HashMap<String, Optional<V>>();
    private boolean hasRemoveAll;
    private final Map<String, DeferredOperation<V>> keyedOperationMap = new HashMap<String, DeferredOperation<V>>();

    protected AbstractExternalCacheRequestContext(ExternalCacheKeyGenerator keyGenerator, String name, Supplier<String> partitionSupplier, Duration lockTimeout) {
        this.keyGenerator = Objects.requireNonNull(keyGenerator);
        this.name = Objects.requireNonNull(name);
        this.partitionSupplier = Objects.requireNonNull(partitionSupplier);
        this.globalLock = new VCacheLock(name, lockTimeout);
    }

    protected abstract long cacheVersion();

    protected void clearKeyMaps() {
        this.internalToExternalKeyMap.clear();
    }

    public VCacheLock getGlobalLock() {
        return this.globalLock;
    }

    public String externalEntryKeyFor(String internalKey) {
        String cached = (String)this.internalToExternalKeyMap.get((Object)Objects.requireNonNull(internalKey));
        if (cached != null) {
            return cached;
        }
        String result = this.keyGenerator.entryKey(this.partitionSupplier.get(), this.name, this.cacheVersion(), internalKey);
        this.internalToExternalKeyMap.forcePut((Object)internalKey, (Object)result);
        return result;
    }

    public String internalEntryKeyFor(String externalKey) {
        return Objects.requireNonNull(this.externalToInternalKeyMap.get(externalKey));
    }

    public Optional<Optional<V>> getValueRecorded(String internalKey) {
        return Optional.ofNullable(this.internalKeyToValueMap.get(Objects.requireNonNull(internalKey)));
    }

    public void recordValue(String internalKey, Optional<V> outcome) {
        log.trace("Cache {}, recording value for {}", (Object)this.name, (Object)internalKey);
        this.internalKeyToValueMap.put(Objects.requireNonNull(internalKey), Objects.requireNonNull(outcome));
    }

    public void recordValues(Map<String, V> knownValues) {
        log.trace("Cache {}, recording {} known values", (Object)this.name, (Object)knownValues.size());
        knownValues.forEach((key, value) -> this.internalKeyToValueMap.put((String)key, (Optional<Optional<Object>>)Optional.of(value)));
    }

    public void forgetValue(String internalKey) {
        log.trace("Cache {}, forgetting value for {}", (Object)this.name, (Object)internalKey);
        this.internalKeyToValueMap.remove(internalKey);
    }

    public void forgetAllValues() {
        log.trace("Cache {}, forgetting all values", (Object)this.name);
        this.internalKeyToValueMap.clear();
    }

    public void recordPut(String internalKey, V value, PutPolicy policy) {
        this.recordValue(internalKey, Optional.of(value));
        this.recordPutPolicy(internalKey, value, policy);
    }

    public void recordPutPolicy(String internalKey, V value, PutPolicy policy) {
        this.keyedOperationMap.put(Objects.requireNonNull(internalKey), DeferredOperation.putOperation(value, policy));
    }

    public void recordRemove(Iterable<String> internalKeys) {
        for (String internalKey : internalKeys) {
            this.recordValue(internalKey, Optional.empty());
            this.keyedOperationMap.put(Objects.requireNonNull(internalKey), DeferredOperation.removeOperation());
        }
    }

    public void recordRemoveAll() {
        this.forgetAllValues();
        this.hasRemoveAll = true;
        this.keyedOperationMap.clear();
    }

    public boolean hasRemoveAll() {
        return this.hasRemoveAll;
    }

    public void forgetAll() {
        this.forgetAllValues();
        this.hasRemoveAll = false;
        this.keyedOperationMap.clear();
    }

    public Set<Map.Entry<String, DeferredOperation<V>>> getKeyedOperations() {
        return this.keyedOperationMap.entrySet();
    }

    public boolean hasPendingOperations() {
        return this.hasRemoveAll || !this.keyedOperationMap.isEmpty();
    }

    public static class DeferredOperation<V> {
        private final boolean remove;
        private final CompletionStage<Optional<V>> value;
        private final Optional<PutPolicy> policy;

        private DeferredOperation() {
            this.remove = true;
            this.value = CompletableFuture.completedFuture(Optional.empty());
            this.policy = Optional.empty();
        }

        private DeferredOperation(V value, PutPolicy policy) {
            this((CompletionStage<Optional<V>>)CompletableFuture.completedFuture(Optional.of(value)), policy);
        }

        private DeferredOperation(CompletionStage<Optional<V>> value, PutPolicy policy) {
            this.remove = false;
            this.value = Objects.requireNonNull(value);
            this.policy = Optional.of(policy);
        }

        public boolean isRemove() {
            return this.remove;
        }

        public boolean isPut() {
            return !this.remove;
        }

        public V getValue() {
            return (V)((Optional)VCacheUtils.unsafeJoin(this.value)).get();
        }

        public PutPolicy getPolicy() {
            return this.policy.get();
        }

        public static <V> DeferredOperation<V> removeOperation() {
            return new DeferredOperation<V>();
        }

        public static <V> DeferredOperation<V> putOperation(V value, PutPolicy policy) {
            return new DeferredOperation<V>(value, policy);
        }
    }
}

