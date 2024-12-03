/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.StableReadExternalCache
 *  com.atlassian.vcache.VCacheUtils
 *  com.atlassian.vcache.internal.ExternalCacheExceptionListener
 *  com.atlassian.vcache.internal.MetricLabel
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.StableReadExternalCache;
import com.atlassian.vcache.VCacheUtils;
import com.atlassian.vcache.internal.ExternalCacheExceptionListener;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.VCacheCoreUtils;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;
import com.atlassian.vcache.internal.core.service.AbstractExternalCache;
import com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext;
import com.atlassian.vcache.internal.core.service.FactoryUtils;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStableReadExternalCache<V>
extends AbstractExternalCache<V>
implements StableReadExternalCache<V> {
    private static final Logger log = LoggerFactory.getLogger(AbstractStableReadExternalCache.class);
    protected final MetricsRecorder metricsRecorder;

    protected AbstractStableReadExternalCache(String name, MetricsRecorder metricsRecorder, Duration lockTimeout, ExternalCacheExceptionListener externalCacheExceptionListener) {
        super(name, lockTimeout, externalCacheExceptionListener);
        this.metricsRecorder = Objects.requireNonNull(metricsRecorder);
    }

    protected abstract boolean internalPut(String var1, V var2, PutPolicy var3);

    protected abstract void internalRemoveAll();

    protected abstract void internalRemove(Iterable<String> var1);

    protected abstract V handleCreation(String var1, V var2) throws ExecutionException, InterruptedException;

    protected abstract Optional<V> directGet(String var1);

    protected abstract Map<String, Optional<V>> directGetBulk(Set<String> var1);

    public final CompletionStage<Optional<V>> get(String internalKey) {
        return this.perform(() -> {
            AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
            return cacheContext.getGlobalLock().withLock(() -> this.internalGetWithoutLock(internalKey, cacheContext));
        });
    }

    public final CompletionStage<V> get(String internalKey, Supplier<V> supplier) {
        return this.perform(() -> {
            AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
            Optional existingValue = cacheContext.getGlobalLock().withLock(() -> {
                Optional<V> value = this.internalGetWithoutLock(internalKey, cacheContext);
                if (value.isPresent()) {
                    return value;
                }
                cacheContext.forgetValue(internalKey);
                return Optional.empty();
            });
            return existingValue.orElseGet(() -> this.lambda$get$4((Supplier)supplier, cacheContext, internalKey));
        });
    }

    private Optional<V> internalGetWithoutLock(String internalKey, AbstractExternalCacheRequestContext<V> cacheContext) {
        Optional<Optional<V>> recordedValue = cacheContext.getValueRecorded(internalKey);
        return recordedValue.orElseGet(() -> {
            String externalKey = cacheContext.externalEntryKeyFor(internalKey);
            this.metricsRecorder.record(this.name, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_REMOTE_GET, 1L);
            Optional<V> externalValue = this.directGet(externalKey);
            cacheContext.recordValue(internalKey, externalValue);
            return externalValue;
        });
    }

    public final CompletionStage<Map<String, Optional<V>>> getBulk(Iterable<String> internalKeys) {
        return this.perform(() -> {
            if (VCacheCoreUtils.isEmpty(internalKeys)) {
                return new HashMap();
            }
            AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
            return cacheContext.getGlobalLock().withLock(() -> {
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
        });
    }

    public final CompletionStage<Map<String, V>> getBulk(Function<Set<String>, Map<String, V>> factory, Iterable<String> internalKeys) {
        return this.perform(() -> {
            if (VCacheCoreUtils.isEmpty(internalKeys)) {
                return new HashMap();
            }
            HashMap grandResult = new HashMap();
            HashSet missingInternalKeys = new HashSet();
            AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
            cacheContext.getGlobalLock().withLock(() -> {
                Map knownState = (Map)VCacheUtils.unsafeJoin(this.getBulk(internalKeys));
                knownState.forEach((key, value) -> {
                    if (value.isPresent()) {
                        grandResult.put(key, value.get());
                    } else {
                        missingInternalKeys.add(key);
                        cacheContext.forgetValue((String)key);
                    }
                });
            });
            if (missingInternalKeys.isEmpty()) {
                return grandResult;
            }
            Map candidateValues = (Map)factory.apply(missingInternalKeys);
            FactoryUtils.verifyFactoryResult(candidateValues, missingInternalKeys);
            cacheContext.getGlobalLock().withLock(() -> candidateValues.entrySet().forEach(entry -> {
                Object finalValue;
                this.metricsRecorder.record(this.name, CacheType.EXTERNAL, MetricLabel.NUMBER_OF_REMOTE_GET, 1L);
                try {
                    boolean added = (Boolean)VCacheUtils.unsafeJoin(this.put((String)entry.getKey(), entry.getValue(), PutPolicy.ADD_ONLY));
                    if (added) {
                        finalValue = entry.getValue();
                    } else {
                        log.trace("Was unable to store the candidate value, so needing to retrieve what's there now");
                        finalValue = VCacheUtils.unsafeJoin(this.get((String)entry.getKey(), entry::getValue));
                    }
                }
                catch (Exception ignore) {
                    finalValue = entry.getValue();
                }
                grandResult.put((String)entry.getKey(), finalValue);
                cacheContext.recordValue((String)entry.getKey(), Optional.of(finalValue));
            }));
            return grandResult;
        });
    }

    public final CompletionStage<Boolean> put(String internalKey, V value, PutPolicy policy) {
        return this.perform(() -> {
            AbstractExternalCacheRequestContext<Object> cacheContext = this.ensureCacheContext();
            boolean successful = cacheContext.getGlobalLock().withLock(() -> this.internalPut(internalKey, value, policy));
            if (successful) {
                cacheContext.recordValue(internalKey, Optional.of(value));
            } else {
                cacheContext.forgetValue(internalKey);
            }
            return successful;
        });
    }

    public final CompletionStage<Void> remove(Iterable<String> keys) {
        return this.perform(() -> {
            this.ensureCacheContext().getGlobalLock().withLock(() -> this.internalRemove(keys));
            return null;
        });
    }

    public final CompletionStage<Void> removeAll() {
        return this.perform(() -> {
            AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
            cacheContext.getGlobalLock().withLock(() -> {
                this.internalRemoveAll();
                cacheContext.forgetAllValues();
            });
            return null;
        });
    }

    private Map<String, Optional<V>> checkValuesRecorded(Iterable<String> internalKeys) {
        AbstractExternalCacheRequestContext cacheContext = this.ensureCacheContext();
        return StreamSupport.stream(internalKeys.spliterator(), false).filter(k -> cacheContext.getValueRecorded((String)k).isPresent()).distinct().collect(Collectors.toMap(k -> k, k -> cacheContext.getValueRecorded((String)k).get()));
    }

    private /* synthetic */ Object lambda$get$4(Supplier supplier, AbstractExternalCacheRequestContext cacheContext, String internalKey) {
        Object candidateValue = Objects.requireNonNull(supplier.get());
        return cacheContext.getGlobalLock().withLock(() -> {
            Optional doubleCheck = cacheContext.getValueRecorded(internalKey);
            if (doubleCheck.isPresent() && doubleCheck.get().isPresent()) {
                return doubleCheck.get().get();
            }
            try {
                Object finalValue = this.handleCreation(internalKey, candidateValue);
                cacheContext.recordValue(internalKey, Optional.of(finalValue));
                return finalValue;
            }
            catch (Exception e) {
                cacheContext.recordValue(internalKey, Optional.of(candidateValue));
                return candidateValue;
            }
        });
    }
}

