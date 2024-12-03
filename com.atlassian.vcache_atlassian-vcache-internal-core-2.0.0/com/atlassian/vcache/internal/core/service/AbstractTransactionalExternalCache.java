/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.TransactionalExternalCache
 *  com.atlassian.vcache.internal.ExternalCacheExceptionListener
 *  com.atlassian.vcache.internal.MetricLabel
 *  com.atlassian.vcache.internal.RequestContext
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.TransactionalExternalCache;
import com.atlassian.vcache.internal.ExternalCacheExceptionListener;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.TransactionControl;
import com.atlassian.vcache.internal.core.VCacheCoreUtils;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.service.AbstractExternalCache;
import com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext;
import com.atlassian.vcache.internal.core.service.FactoryUtils;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractTransactionalExternalCache<V>
extends AbstractExternalCache<V>
implements TransactionalExternalCache<V>,
TransactionControl {
    protected final Supplier<RequestContext> contextSupplier;
    protected final MetricsRecorder metricsRecorder;

    protected AbstractTransactionalExternalCache(String name, Supplier<RequestContext> contextSupplier, MetricsRecorder metricsRecorder, Duration lockTimeout, ExternalCacheExceptionListener externalCacheExceptionListener) {
        super(name, lockTimeout, externalCacheExceptionListener);
        this.contextSupplier = Objects.requireNonNull(contextSupplier);
        this.metricsRecorder = Objects.requireNonNull(metricsRecorder);
    }

    protected abstract Optional<V> directGet(String var1);

    protected abstract Map<String, Optional<V>> directGetBulk(Set<String> var1);

    public final CompletionStage<Optional<V>> get(String internalKey) {
        return this.perform(() -> {
            AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
            Optional recordedValue = cacheContext.getValueRecorded(internalKey);
            return recordedValue.orElseGet(() -> {
                if (cacheContext.hasRemoveAll()) {
                    return Optional.empty();
                }
                String externalKey = cacheContext.externalEntryKeyFor(internalKey);
                this.metricsRecorder.record(this.name, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_REMOTE_GET, 1L);
                Optional<V> externalValue = this.directGet(externalKey);
                cacheContext.recordValue(internalKey, externalValue);
                return externalValue;
            });
        });
    }

    public final CompletionStage<V> get(String internalKey, Supplier<V> supplier) {
        return this.perform(() -> {
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            String externalKey = cacheContext.externalEntryKeyFor(internalKey);
            Optional recordedValue = cacheContext.getValueRecorded(internalKey);
            if (recordedValue.isPresent()) {
                if (recordedValue.get().isPresent()) {
                    return recordedValue.get().get();
                }
                this.getLogger().trace("Cache {}, creating candidate for key {}", (Object)this.name, (Object)internalKey);
                return this.handleCreation(internalKey, supplier);
            }
            if (!cacheContext.hasRemoveAll()) {
                this.metricsRecorder.record(this.name, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_REMOTE_GET, 1L);
                Optional<V> result = this.directGet(externalKey);
                if (result.isPresent()) {
                    cacheContext.recordValue(internalKey, result);
                    return result.get();
                }
            }
            this.getLogger().trace("Cache {}, creating candidate for key {}", (Object)this.name, (Object)internalKey);
            return this.handleCreation(internalKey, supplier);
        });
    }

    public final CompletionStage<Map<String, Optional<V>>> getBulk(Iterable<String> internalKeys) {
        return this.perform(() -> {
            if (VCacheCoreUtils.isEmpty(internalKeys)) {
                return new HashMap();
            }
            AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
            Map grandResult = this.checkValuesRecorded(internalKeys);
            Set<String> missingExternalKeys = StreamSupport.stream(internalKeys.spliterator(), false).filter(k -> !grandResult.containsKey(k)).map(cacheContext::externalEntryKeyFor).collect(Collectors.toSet());
            if (missingExternalKeys.isEmpty()) {
                this.getLogger().trace("Cache {}: getBulk(): have all the requested entries cached", (Object)this.name);
                return grandResult;
            }
            this.getLogger().trace("Cache {}: getBulk(): not cached {} requested entries", (Object)this.name, (Object)missingExternalKeys.size());
            this.metricsRecorder.record(this.name, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_REMOTE_GET, 1L);
            Map<String, Optional<V>> candidateValues = this.directGetBulk(missingExternalKeys);
            return candidateValues.entrySet().stream().collect(() -> grandResult, (m, e) -> {
                Optional result = (Optional)e.getValue();
                cacheContext.recordValue(cacheContext.internalEntryKeyFor((String)e.getKey()), result);
                m.put(cacheContext.internalEntryKeyFor((String)e.getKey()), result);
            }, Map::putAll);
        });
    }

    public final CompletionStage<Map<String, V>> getBulk(Function<Set<String>, Map<String, V>> factory, Iterable<String> internalKeys) {
        return this.perform(() -> {
            if (VCacheCoreUtils.isEmpty(internalKeys)) {
                return new HashMap();
            }
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            Map<String, Object> grandResult = this.checkValuesRecorded(internalKeys).entrySet().stream().filter(e -> ((Optional)e.getValue()).isPresent()).collect(Collectors.toMap(Map.Entry::getKey, e -> ((Optional)e.getValue()).get()));
            Set<String> candidateMissingExternalKeys = StreamSupport.stream(internalKeys.spliterator(), false).filter(k -> !grandResult.containsKey(k)).map(cacheContext::externalEntryKeyFor).collect(Collectors.toSet());
            if (candidateMissingExternalKeys.isEmpty()) {
                this.getLogger().trace("Cache {}: getBulk(Function): had all the requested entries cached", (Object)this.name);
                return grandResult;
            }
            this.getLogger().trace("Cache {}: getBulk(Function): checking external cache for {} keys", (Object)this.name, (Object)candidateMissingExternalKeys.size());
            Map<String, V> missingValues = this.handleCreation(factory, candidateMissingExternalKeys);
            cacheContext.recordValues(missingValues);
            grandResult.putAll(missingValues);
            return grandResult;
        });
    }

    public final void put(String internalKey, V value, PutPolicy policy) {
        AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
        cacheContext.recordPut(internalKey, value, policy);
    }

    public final void remove(Iterable<String> internalKeys) {
        AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
        cacheContext.recordRemove(internalKeys);
    }

    public final void removeAll() {
        AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
        cacheContext.recordRemoveAll();
    }

    @Override
    public final boolean transactionDiscard() {
        RequestContext requestContext = this.contextSupplier.get();
        Optional cacheRequestContext = requestContext.get((Object)this);
        if (!cacheRequestContext.isPresent()) {
            return false;
        }
        boolean hasPendingOperations = ((AbstractExternalCacheRequestContext)cacheRequestContext.get()).hasPendingOperations();
        ((AbstractExternalCacheRequestContext)cacheRequestContext.get()).forgetAll();
        return hasPendingOperations;
    }

    private Map<String, Optional<V>> checkValuesRecorded(Iterable<String> internalKeys) {
        AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
        HashMap result = new HashMap();
        internalKeys.forEach(k -> {
            Optional valueRecorded = cacheContext.getValueRecorded((String)k);
            if (valueRecorded.isPresent()) {
                result.put((String)k, valueRecorded.get());
            } else if (cacheContext.hasRemoveAll()) {
                result.put((String)k, Optional.empty());
            }
        });
        return result;
    }

    private V handleCreation(String internalKey, Supplier<V> supplier) throws ExecutionException, InterruptedException {
        AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
        V suppliedValue = Objects.requireNonNull(supplier.get());
        cacheContext.recordPutPolicy(internalKey, suppliedValue, PutPolicy.ADD_ONLY);
        cacheContext.recordValue(internalKey, Optional.of(suppliedValue));
        return suppliedValue;
    }

    private Map<String, V> handleCreation(Function<Set<String>, Map<String, V>> factory, Set<String> externalKeys) throws ExecutionException, InterruptedException {
        HashMap<String, V> grandResult;
        AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
        Set<String> missingExternalKeys = this.fillInKnownValuesFromBackingCache(cacheContext, externalKeys, grandResult = new HashMap<String, V>());
        if (!missingExternalKeys.isEmpty()) {
            this.getLogger().trace("Cache {}: getBulk(Function): calling factory to create {} values", (Object)this.name, (Object)missingExternalKeys.size());
            Set missingInternalKeys = Collections.unmodifiableSet(missingExternalKeys.stream().map(cacheContext::internalEntryKeyFor).collect(Collectors.toSet()));
            Map<String, V> missingValues = factory.apply(missingInternalKeys);
            FactoryUtils.verifyFactoryResult(missingValues, missingInternalKeys);
            missingValues.entrySet().forEach(e -> this.put((String)e.getKey(), e.getValue(), PutPolicy.ADD_ONLY));
            grandResult.putAll(missingValues);
        }
        return grandResult;
    }

    private Set<String> fillInKnownValuesFromBackingCache(AbstractExternalCacheRequestContext<V> cacheContext, Set<String> externalKeys, Map<String, V> grandResult) {
        Set<String> missingExternalKeys;
        if (cacheContext.hasRemoveAll()) {
            missingExternalKeys = externalKeys;
        } else {
            missingExternalKeys = externalKeys.stream().filter(k -> {
                Optional valueRecorded = cacheContext.getValueRecorded(cacheContext.internalEntryKeyFor((String)k));
                return valueRecorded.isPresent();
            }).collect(Collectors.toSet());
            Set<String> externalKeysNotRemoved = externalKeys.stream().filter(k -> !missingExternalKeys.contains(k)).collect(Collectors.toSet());
            if (!externalKeysNotRemoved.isEmpty()) {
                this.metricsRecorder.record(this.name, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_REMOTE_GET, 1L);
                Map<String, Optional<V>> candidateValues = this.directGetBulk(externalKeysNotRemoved);
                candidateValues.entrySet().forEach(e -> {
                    if (((Optional)e.getValue()).isPresent()) {
                        grandResult.put(cacheContext.internalEntryKeyFor((String)e.getKey()), ((Optional)e.getValue()).get());
                    } else {
                        missingExternalKeys.add((String)e.getKey());
                    }
                });
            }
        }
        return missingExternalKeys;
    }
}

