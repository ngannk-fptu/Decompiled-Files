/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.ChangeRate
 *  com.atlassian.vcache.DirectExternalCache
 *  com.atlassian.vcache.ExternalCache
 *  com.atlassian.vcache.ExternalCacheException
 *  com.atlassian.vcache.ExternalCacheException$Reason
 *  com.atlassian.vcache.ExternalCacheSettings
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.JvmCacheSettings
 *  com.atlassian.vcache.Marshaller
 *  com.atlassian.vcache.RequestCache
 *  com.atlassian.vcache.RequestCacheSettings
 *  com.atlassian.vcache.StableReadExternalCache
 *  com.atlassian.vcache.TransactionalExternalCache
 *  com.atlassian.vcache.VCacheFactory
 *  com.atlassian.vcache.internal.BegunTransactionalActivityHandler
 *  com.atlassian.vcache.internal.ExternalCacheDetails
 *  com.atlassian.vcache.internal.ExternalCacheDetails$BufferPolicy
 *  com.atlassian.vcache.internal.JvmCacheDetails
 *  com.atlassian.vcache.internal.NameValidator
 *  com.atlassian.vcache.internal.RequestCacheDetails
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.RequestMetrics
 *  com.atlassian.vcache.internal.VCacheCreationHandler
 *  com.atlassian.vcache.internal.VCacheLifecycleManager
 *  com.atlassian.vcache.internal.VCacheManagement
 *  com.atlassian.vcache.internal.VCacheSettingsDefaultsProvider
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.ChangeRate;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.ExternalCache;
import com.atlassian.vcache.ExternalCacheException;
import com.atlassian.vcache.ExternalCacheSettings;
import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.Marshaller;
import com.atlassian.vcache.RequestCache;
import com.atlassian.vcache.RequestCacheSettings;
import com.atlassian.vcache.StableReadExternalCache;
import com.atlassian.vcache.TransactionalExternalCache;
import com.atlassian.vcache.VCacheFactory;
import com.atlassian.vcache.internal.BegunTransactionalActivityHandler;
import com.atlassian.vcache.internal.ExternalCacheDetails;
import com.atlassian.vcache.internal.JvmCacheDetails;
import com.atlassian.vcache.internal.NameValidator;
import com.atlassian.vcache.internal.RequestCacheDetails;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.RequestMetrics;
import com.atlassian.vcache.internal.VCacheCreationHandler;
import com.atlassian.vcache.internal.VCacheLifecycleManager;
import com.atlassian.vcache.internal.VCacheManagement;
import com.atlassian.vcache.internal.VCacheSettingsDefaultsProvider;
import com.atlassian.vcache.internal.core.DefaultExternalCacheDetails;
import com.atlassian.vcache.internal.core.DefaultJvmCacheDetails;
import com.atlassian.vcache.internal.core.DefaultRequestCacheDetails;
import com.atlassian.vcache.internal.core.DefaultTransactionControlManager;
import com.atlassian.vcache.internal.core.ExternalCacheKeyGenerator;
import com.atlassian.vcache.internal.core.TransactionControlManager;
import com.atlassian.vcache.internal.core.metrics.MetricsCollector;
import com.atlassian.vcache.internal.core.service.DefaultRequestCache;
import com.atlassian.vcache.internal.core.service.ReadOptimisedRequestCache;
import com.google.common.annotations.VisibleForTesting;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;

public abstract class AbstractVCacheService
implements VCacheFactory,
VCacheManagement,
VCacheLifecycleManager {
    private static final Set<String> SERIALIZABLE_MARSHALLER_CLASS_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("com.atlassian.vcache.marshallers.StringMarshaller", "com.atlassian.vcache.marshallers.JavaSerializationMarshaller")));
    private static final Set<String> SERIALIZABLE_MARSHALLING_CLASS_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("com.atlassian.marshalling.jdk.JavaSerializationMarshalling", "com.atlassian.marshalling.jdk.StringMarshalling")));
    protected final Supplier<RequestContext> workContextContextSupplier;
    protected final Supplier<RequestContext> threadLocalContextSupplier;
    protected final TransactionControlManager transactionControlManager;
    protected final MetricsCollector metricsCollector;
    protected final ExternalCacheKeyGenerator externalCacheKeyGenerator;
    protected final Duration lockTimeout;
    private final VCacheSettingsDefaultsProvider defaultsProvider;
    private final VCacheCreationHandler creationHandler;
    private final ConcurrentHashMap<String, JvmCache> jvmCacheInstancesMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, JvmCacheDetails> jvmCacheDetailsMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, RequestCache> requestCacheInstancesMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, RequestCacheDetails> requestCacheDetailsMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, ExternalCache> externalCacheInstancesMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, ExternalCacheDetails> externalCacheDetailsMap = new ConcurrentHashMap();

    public AbstractVCacheService(Supplier<RequestContext> threadLocalContextSupplier, Supplier<RequestContext> workContextContextSupplier, VCacheSettingsDefaultsProvider defaultsProvider, VCacheCreationHandler creationHandler, MetricsCollector metricsCollector, ExternalCacheKeyGenerator externalCacheKeyGenerator, BegunTransactionalActivityHandler begunTransactionalActivityHandler, Duration lockTimeout) {
        this.threadLocalContextSupplier = Objects.requireNonNull(threadLocalContextSupplier);
        this.workContextContextSupplier = Objects.requireNonNull(workContextContextSupplier);
        this.defaultsProvider = Objects.requireNonNull(defaultsProvider);
        this.creationHandler = Objects.requireNonNull(creationHandler);
        this.metricsCollector = Objects.requireNonNull(metricsCollector);
        this.transactionControlManager = new DefaultTransactionControlManager(metricsCollector, begunTransactionalActivityHandler);
        this.externalCacheKeyGenerator = Objects.requireNonNull(externalCacheKeyGenerator);
        this.lockTimeout = Objects.requireNonNull(lockTimeout);
    }

    protected abstract Logger log();

    protected abstract <K, V> JvmCache<K, V> createJvmCache(String var1, JvmCacheSettings var2);

    protected abstract <V> TransactionalExternalCache<V> createTransactionalExternalCache(String var1, ExternalCacheSettings var2, MarshallingPair<V> var3, boolean var4);

    protected abstract <V> StableReadExternalCache<V> createStableReadExternalCache(String var1, ExternalCacheSettings var2, MarshallingPair<V> var3, boolean var4);

    protected abstract <V> DirectExternalCache<V> createDirectExternalCache(String var1, ExternalCacheSettings var2, MarshallingPair<V> var3, boolean var4);

    public <K, V> JvmCache<K, V> getJvmCache(String name, JvmCacheSettings settings) {
        return this.jvmCacheInstancesMap.computeIfAbsent(NameValidator.requireValidCacheName((String)name), s -> {
            this.log().trace("Cache {}: creating the instance", (Object)name);
            JvmCacheSettings candidateSettings = this.defaultsProvider.getJvmDefaults(name).override(settings);
            JvmCacheSettings finalSettings = this.creationHandler.jvmCacheCreation((JvmCacheDetails)new DefaultJvmCacheDetails(name, candidateSettings));
            this.jvmCacheDetailsMap.put(name, new DefaultJvmCacheDetails(name, finalSettings));
            return this.metricsCollector.wrap(this.createJvmCache(name, finalSettings));
        });
    }

    public <K, V> RequestCache<K, V> getRequestCache(String name, RequestCacheSettings settings) {
        return this.requestCacheInstancesMap.computeIfAbsent(NameValidator.requireValidCacheName((String)name), s -> {
            this.log().trace("DefaultRequestCache {}: creating the instance", (Object)name);
            this.creationHandler.requestCacheCreation(name);
            this.requestCacheDetailsMap.put(name, new DefaultRequestCacheDetails(name));
            if (settings.getChangeRate().equals((Object)ChangeRate.NONE)) {
                return this.metricsCollector.wrap(new ReadOptimisedRequestCache(name, this.workContextContextSupplier, this.lockTimeout));
            }
            return this.metricsCollector.wrap(new DefaultRequestCache(name, this.workContextContextSupplier, this.lockTimeout));
        });
    }

    public <V> TransactionalExternalCache<V> getTransactionalExternalCache(String name, MarshallingPair<V> valueMarshalling, ExternalCacheSettings settings) {
        MarshallingPair wrappedMarshalling = this.metricsCollector.wrap(valueMarshalling, name);
        TransactionalExternalCache cache = this.obtainCache(name, ExternalCacheDetails.BufferPolicy.FULLY, settings, finalSettings -> this.createTransactionalExternalCache(name, (ExternalCacheSettings)finalSettings, wrappedMarshalling, AbstractVCacheService.isValueSerializable(valueMarshalling)));
        return this.metricsCollector.wrap(cache);
    }

    public <V> TransactionalExternalCache<V> getTransactionalExternalCache(String name, Marshaller<V> valueMarshaller, ExternalCacheSettings settings) {
        MarshallingPair marshallingPair = new MarshallingPair(valueMarshaller, valueMarshaller);
        MarshallingPair wrappedMarshalling = this.metricsCollector.wrap(marshallingPair, name);
        TransactionalExternalCache cache = this.obtainCache(name, ExternalCacheDetails.BufferPolicy.FULLY, settings, finalSettings -> this.createTransactionalExternalCache(name, (ExternalCacheSettings)finalSettings, (MarshallingPair)wrappedMarshalling, AbstractVCacheService.isValueSerializable(valueMarshaller)));
        return this.metricsCollector.wrap(cache);
    }

    public <V> StableReadExternalCache<V> getStableReadExternalCache(String name, MarshallingPair<V> valueMarshalling, ExternalCacheSettings settings) {
        MarshallingPair wrappedMarshalling = this.metricsCollector.wrap(valueMarshalling, name);
        StableReadExternalCache cache = this.obtainCache(name, ExternalCacheDetails.BufferPolicy.READ_ONLY, settings, finalSettings -> this.createStableReadExternalCache(name, (ExternalCacheSettings)finalSettings, wrappedMarshalling, AbstractVCacheService.isValueSerializable(valueMarshalling)));
        return this.metricsCollector.wrap(cache);
    }

    public <V> StableReadExternalCache<V> getStableReadExternalCache(String name, Marshaller<V> valueMarshaller, ExternalCacheSettings settings) {
        MarshallingPair marshallingPair = new MarshallingPair(valueMarshaller, valueMarshaller);
        MarshallingPair wrappedMarshalling = this.metricsCollector.wrap(marshallingPair, name);
        StableReadExternalCache cache = this.obtainCache(name, ExternalCacheDetails.BufferPolicy.READ_ONLY, settings, finalSettings -> this.createStableReadExternalCache(name, (ExternalCacheSettings)finalSettings, (MarshallingPair)wrappedMarshalling, AbstractVCacheService.isValueSerializable(valueMarshaller)));
        return this.metricsCollector.wrap(cache);
    }

    public <V> DirectExternalCache<V> getDirectExternalCache(String name, MarshallingPair<V> valueMarshalling, ExternalCacheSettings settings) {
        MarshallingPair wrappedMarshalling = this.metricsCollector.wrap(valueMarshalling, name);
        DirectExternalCache cache = this.obtainCache(name, ExternalCacheDetails.BufferPolicy.NEVER, settings, finalSettings -> this.createDirectExternalCache(name, (ExternalCacheSettings)finalSettings, wrappedMarshalling, AbstractVCacheService.isValueSerializable(valueMarshalling)));
        return this.metricsCollector.wrap(cache);
    }

    public <V> DirectExternalCache<V> getDirectExternalCache(String name, Marshaller<V> valueMarshaller, ExternalCacheSettings settings) {
        MarshallingPair marshallingPair = new MarshallingPair(valueMarshaller, valueMarshaller);
        MarshallingPair wrappedMarshalling = this.metricsCollector.wrap(marshallingPair, name);
        DirectExternalCache cache = this.obtainCache(name, ExternalCacheDetails.BufferPolicy.NEVER, settings, finalSettings -> this.createDirectExternalCache(name, (ExternalCacheSettings)finalSettings, (MarshallingPair)wrappedMarshalling, AbstractVCacheService.isValueSerializable(valueMarshaller)));
        return this.metricsCollector.wrap(cache);
    }

    public void transactionSync(RequestContext context) {
        this.transactionControlManager.syncAll(context);
    }

    public Set<String> transactionDiscard(RequestContext context) {
        return this.transactionControlManager.discardAll(context);
    }

    public RequestMetrics metrics(RequestContext context) {
        return this.metricsCollector.obtainRequestMetrics(context);
    }

    public Map<String, JvmCacheDetails> allJvmCacheDetails() {
        HashMap<String, JvmCacheDetails> result = new HashMap<String, JvmCacheDetails>();
        result.putAll(this.jvmCacheDetailsMap);
        return result;
    }

    public Map<String, RequestCacheDetails> allRequestCacheDetails() {
        HashMap<String, RequestCacheDetails> result = new HashMap<String, RequestCacheDetails>();
        result.putAll(this.requestCacheDetailsMap);
        return result;
    }

    public Map<String, ExternalCacheDetails> allExternalCacheDetails() {
        HashMap<String, ExternalCacheDetails> result = new HashMap<String, ExternalCacheDetails>();
        result.putAll(this.externalCacheDetailsMap);
        return result;
    }

    @VisibleForTesting
    static <V> boolean isValueSerializable(Marshaller<V> valueMarshaller) {
        return SERIALIZABLE_MARSHALLER_CLASS_NAMES.contains(valueMarshaller.getClass().getName());
    }

    @VisibleForTesting
    static <V> boolean isValueSerializable(MarshallingPair<V> valueMarshalling) {
        return SERIALIZABLE_MARSHALLING_CLASS_NAMES.contains(valueMarshalling.getMarshaller().getClass().getName()) && SERIALIZABLE_MARSHALLING_CLASS_NAMES.contains(valueMarshalling.getUnmarshaller().getClass().getName());
    }

    private <C extends ExternalCache> C obtainCache(String name, ExternalCacheDetails.BufferPolicy policy, ExternalCacheSettings settings, Function<ExternalCacheSettings, C> factory) {
        ExternalCache candidateCache = this.externalCacheInstancesMap.compute(NameValidator.requireValidCacheName((String)name), (key, existing) -> {
            if (existing != null && this.externalCacheDetailsMap.get(name).getPolicy() != policy) {
                this.log().warn("Cache {}: unable to create cache with policy {}, as one already configured with policy {}", new Object[]{name, ExternalCacheDetails.BufferPolicy.READ_ONLY, this.externalCacheDetailsMap.get(name).getPolicy()});
                throw new ExternalCacheException(ExternalCacheException.Reason.CREATION_FAILURE);
            }
            this.log().trace("Cache {}: creating the instance with policy {}", (Object)name, (Object)policy);
            ExternalCacheSettings candidateSettings = this.defaultsProvider.getExternalDefaults(name).override(settings);
            ExternalCacheSettings finalSettings = this.creationHandler.externalCacheCreation((ExternalCacheDetails)new DefaultExternalCacheDetails(name, policy, candidateSettings));
            this.externalCacheDetailsMap.put(name, new DefaultExternalCacheDetails(name, policy, finalSettings));
            return (ExternalCache)factory.apply(finalSettings);
        });
        return (C)candidateCache;
    }
}

