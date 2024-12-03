/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.CasIdentifier
 *  com.atlassian.vcache.DirectExternalCache
 *  com.atlassian.vcache.ExternalCacheException
 *  com.atlassian.vcache.IdentifiedValue
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.VCacheUtils
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator
 *  com.atlassian.vcache.internal.core.VCacheCoreUtils
 *  com.atlassian.vcache.internal.core.cas.IdentifiedData
 *  com.atlassian.vcache.internal.core.cas.IdentifiedUtils
 *  com.atlassian.vcache.internal.core.service.AbstractExternalCache
 *  com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext
 *  com.atlassian.vcache.internal.core.service.FactoryUtils
 *  com.atlassian.vcache.internal.core.service.UnversionedExternalCacheRequestContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.legacy;

import com.atlassian.cache.Cache;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.CasIdentifier;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.ExternalCacheException;
import com.atlassian.vcache.IdentifiedValue;
import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.VCacheUtils;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.VCacheCoreUtils;
import com.atlassian.vcache.internal.core.cas.IdentifiedData;
import com.atlassian.vcache.internal.core.cas.IdentifiedUtils;
import com.atlassian.vcache.internal.core.service.AbstractExternalCache;
import com.atlassian.vcache.internal.core.service.AbstractExternalCacheRequestContext;
import com.atlassian.vcache.internal.core.service.FactoryUtils;
import com.atlassian.vcache.internal.core.service.UnversionedExternalCacheRequestContext;
import com.atlassian.vcache.internal.legacy.LegacyServiceSettings;
import com.atlassian.vcache.internal.legacy.LegacyUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LegacyDirectExternalCache<V>
extends AbstractExternalCache<V>
implements DirectExternalCache<V> {
    private static final Logger log = LoggerFactory.getLogger(LegacyDirectExternalCache.class);
    private final Cache<String, IdentifiedData> delegate;
    private final Supplier<RequestContext> contextSupplier;
    private final ExternalCacheKeyGenerator keyGenerator;
    private final Optional<MarshallingPair<V>> valueMarshalling;
    private final LegacyServiceSettings serviceSettings;

    LegacyDirectExternalCache(Cache<String, IdentifiedData> delegate, Supplier<RequestContext> contextSupplier, ExternalCacheKeyGenerator keyGenerator, Optional<MarshallingPair<V>> valueMarshalling, LegacyServiceSettings serviceSettings) {
        super(delegate.getName(), serviceSettings.getLockTimeout(), (n, ex) -> {});
        this.delegate = Objects.requireNonNull(delegate);
        this.contextSupplier = Objects.requireNonNull(contextSupplier);
        this.keyGenerator = Objects.requireNonNull(keyGenerator);
        this.valueMarshalling = Objects.requireNonNull(valueMarshalling);
        this.serviceSettings = Objects.requireNonNull(serviceSettings);
    }

    public CompletionStage<Optional<V>> get(String internalKey) {
        return this.perform(() -> {
            String externalKey = this.buildExternalKey(internalKey);
            IdentifiedData identifiedData = (IdentifiedData)this.delegate.get((Object)externalKey);
            return IdentifiedUtils.unmarshall((IdentifiedData)identifiedData, this.valueMarshalling);
        });
    }

    public CompletionStage<V> get(String internalKey, Supplier<V> supplier) {
        return this.perform(() -> {
            String externalKey = this.buildExternalKey(internalKey);
            IdentifiedData identifiedData = (IdentifiedData)this.delegate.get((Object)externalKey, () -> this.lambda$get$2((Supplier)supplier));
            return IdentifiedUtils.unmarshall((IdentifiedData)identifiedData, this.valueMarshalling).get();
        });
    }

    public CompletionStage<Optional<IdentifiedValue<V>>> getIdentified(String internalKey) {
        return this.perform(() -> {
            this.verifyCasOpsSupported();
            String externalKey = this.buildExternalKey(internalKey);
            return IdentifiedUtils.unmarshallIdentified((IdentifiedData)((IdentifiedData)this.delegate.get((Object)externalKey)), this.valueMarshalling);
        });
    }

    public CompletionStage<IdentifiedValue<V>> getIdentified(String internalKey, Supplier<V> supplier) {
        return this.perform(() -> {
            this.verifyCasOpsSupported();
            String externalKey = this.buildExternalKey(internalKey);
            return (IdentifiedValue)IdentifiedUtils.unmarshallIdentified((IdentifiedData)((IdentifiedData)this.delegate.get((Object)externalKey, () -> this.lambda$getIdentified$5((Supplier)supplier))), this.valueMarshalling).get();
        });
    }

    public CompletionStage<Map<String, Optional<V>>> getBulk(Iterable<String> internalKeys) {
        return this.perform(() -> {
            if (VCacheCoreUtils.isEmpty((Iterable)internalKeys)) {
                return new HashMap();
            }
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            return StreamSupport.stream(internalKeys.spliterator(), false).distinct().collect(Collectors.toMap(Objects::requireNonNull, k -> IdentifiedUtils.unmarshall((IdentifiedData)((IdentifiedData)this.delegate.get((Object)cacheContext.externalEntryKeyFor(k))), this.valueMarshalling)));
        });
    }

    public CompletionStage<Map<String, V>> getBulk(Function<Set<String>, Map<String, V>> factory, Iterable<String> internalKeys) {
        return this.perform(() -> {
            if (VCacheCoreUtils.isEmpty((Iterable)internalKeys)) {
                return new HashMap();
            }
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            Map existingValues = (Map)VCacheUtils.unsafeJoin(this.getBulk(internalKeys));
            Map<String, Object> grandResult = existingValues.entrySet().stream().filter(e -> ((Optional)e.getValue()).isPresent()).collect(Collectors.toMap(Map.Entry::getKey, e -> ((Optional)e.getValue()).get()));
            if (grandResult.size() == existingValues.size()) {
                return grandResult;
            }
            Set missingInternalKeys = existingValues.entrySet().stream().filter(e -> !((Optional)e.getValue()).isPresent()).map(Map.Entry::getKey).collect(Collectors.toSet());
            Map missingValues = (Map)factory.apply(missingInternalKeys);
            FactoryUtils.verifyFactoryResult((Map)missingValues, missingInternalKeys);
            missingValues.entrySet().forEach(e -> {
                if (this.serviceSettings.isAvoidCasOps()) {
                    this.delegate.put((Object)cacheContext.externalEntryKeyFor((String)e.getKey()), (Object)IdentifiedUtils.marshall(e.getValue(), this.valueMarshalling));
                    grandResult.put((String)e.getKey(), e.getValue());
                } else {
                    Optional existing = IdentifiedUtils.unmarshall((IdentifiedData)((IdentifiedData)this.delegate.putIfAbsent((Object)cacheContext.externalEntryKeyFor((String)e.getKey()), (Object)IdentifiedUtils.marshall(e.getValue(), this.valueMarshalling))), this.valueMarshalling);
                    grandResult.put((String)e.getKey(), existing.orElse(e.getValue()));
                }
            });
            return grandResult;
        });
    }

    public CompletionStage<Map<String, Optional<IdentifiedValue<V>>>> getBulkIdentified(Iterable<String> internalKeys) {
        return this.perform(() -> {
            this.verifyCasOpsSupported();
            if (VCacheCoreUtils.isEmpty((Iterable)internalKeys)) {
                return new HashMap();
            }
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            return StreamSupport.stream(internalKeys.spliterator(), false).distinct().collect(Collectors.toMap(Objects::requireNonNull, k -> IdentifiedUtils.unmarshallIdentified((IdentifiedData)((IdentifiedData)this.delegate.get((Object)cacheContext.externalEntryKeyFor(k))), this.valueMarshalling)));
        });
    }

    public CompletionStage<Boolean> put(String internalKey, V value, PutPolicy policy) {
        return this.perform(() -> {
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            String externalKey = cacheContext.externalEntryKeyFor(internalKey);
            IdentifiedData identifiedData = IdentifiedUtils.marshall((Object)value, this.valueMarshalling);
            return LegacyUtils.directPut(externalKey, identifiedData, policy, this.delegate, this.serviceSettings.isAvoidCasOps());
        });
    }

    public CompletionStage<Boolean> removeIf(String internalKey, CasIdentifier casId) {
        return this.perform(() -> {
            this.verifyCasOpsSupported();
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            String externalKey = cacheContext.externalEntryKeyFor(internalKey);
            IdentifiedData existingData = IdentifiedUtils.safeCast((CasIdentifier)casId);
            return this.delegate.remove((Object)externalKey, (Object)existingData);
        });
    }

    public CompletionStage<Boolean> replaceIf(String internalKey, CasIdentifier casId, V newValue) {
        return this.perform(() -> {
            this.verifyCasOpsSupported();
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            String externalKey = cacheContext.externalEntryKeyFor(internalKey);
            IdentifiedData existingData = IdentifiedUtils.safeCast((CasIdentifier)casId);
            IdentifiedData newData = IdentifiedUtils.marshall((Object)newValue, this.valueMarshalling);
            return this.delegate.replace((Object)externalKey, (Object)existingData, (Object)newData);
        });
    }

    public CompletionStage<Void> remove(Iterable<String> internalKeys) {
        return this.perform(() -> {
            AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
            StreamSupport.stream(internalKeys.spliterator(), false).distinct().map(arg_0 -> cacheContext.externalEntryKeyFor(arg_0)).forEach(arg_0 -> this.delegate.remove(arg_0));
            return null;
        });
    }

    public CompletionStage<Void> removeAll() {
        return this.perform(() -> {
            this.delegate.removeAll();
            return null;
        });
    }

    protected AbstractExternalCacheRequestContext<V> ensureCacheContext() {
        RequestContext requestContext = this.contextSupplier.get();
        return (AbstractExternalCacheRequestContext)requestContext.computeIfAbsent((Object)this, () -> {
            log.trace("Cache {}: Setting up a new context", (Object)this.delegate.getName());
            return new UnversionedExternalCacheRequestContext(this.keyGenerator, this.delegate.getName(), () -> ((RequestContext)requestContext).partitionIdentifier(), this.serviceSettings.getLockTimeout());
        });
    }

    protected ExternalCacheException mapException(Exception ex) {
        return LegacyUtils.mapException(ex);
    }

    protected Logger getLogger() {
        return log;
    }

    private String buildExternalKey(String internalKey) {
        AbstractExternalCacheRequestContext<V> cacheContext = this.ensureCacheContext();
        return cacheContext.externalEntryKeyFor(internalKey);
    }

    private void verifyCasOpsSupported() {
        if (this.serviceSettings.isAvoidCasOps()) {
            throw new UnsupportedOperationException("CAS operations not supported in this configuration");
        }
    }

    private /* synthetic */ IdentifiedData lambda$getIdentified$5(Supplier supplier) {
        return IdentifiedUtils.marshall(supplier.get(), this.valueMarshalling);
    }

    private /* synthetic */ IdentifiedData lambda$get$2(Supplier supplier) {
        return IdentifiedUtils.marshall(supplier.get(), this.valueMarshalling);
    }
}

