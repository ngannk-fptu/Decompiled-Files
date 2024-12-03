/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.vcache.ChangeRate
 *  com.atlassian.vcache.VCacheFactory
 *  com.atlassian.vcache.internal.BegunTransactionalActivityHandler
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.VCacheCreationHandler
 *  com.atlassian.vcache.internal.VCacheLifecycleManager
 *  com.atlassian.vcache.internal.VCacheSettingsDefaultsProvider
 *  com.atlassian.vcache.internal.core.DefaultVCacheCreationHandler
 *  com.atlassian.vcache.internal.core.metrics.MetricsCollector
 *  com.atlassian.vcache.internal.core.metrics.SamplingMetricsCollector
 *  com.atlassian.vcache.internal.legacy.LegacyServiceSettings
 *  com.atlassian.vcache.internal.legacy.LegacyServiceSettingsBuilder
 *  com.atlassian.vcache.internal.legacy.LegacyVCacheService
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.impl.vcache.CollectMetricsPredicate;
import com.atlassian.confluence.impl.vcache.VCacheFlusher;
import com.atlassian.confluence.impl.vcache.VCacheRequestContextManager;
import com.atlassian.confluence.impl.vcache.VCacheSettingsProvider;
import com.atlassian.confluence.impl.vcache.VCacheTransactionSyncHandler;
import com.atlassian.vcache.ChangeRate;
import com.atlassian.vcache.VCacheFactory;
import com.atlassian.vcache.internal.BegunTransactionalActivityHandler;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.VCacheCreationHandler;
import com.atlassian.vcache.internal.VCacheLifecycleManager;
import com.atlassian.vcache.internal.VCacheSettingsDefaultsProvider;
import com.atlassian.vcache.internal.core.DefaultVCacheCreationHandler;
import com.atlassian.vcache.internal.core.metrics.MetricsCollector;
import com.atlassian.vcache.internal.core.metrics.SamplingMetricsCollector;
import com.atlassian.vcache.internal.legacy.LegacyServiceSettings;
import com.atlassian.vcache.internal.legacy.LegacyServiceSettingsBuilder;
import com.atlassian.vcache.internal.legacy.LegacyVCacheService;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.beans.factory.FactoryBean;

public class VCacheFactoryBean
implements FactoryBean {
    private static final String PRODUCT_IDENTIFIER = "confluence";
    private final CacheFactory cacheFactory;
    private final CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider;
    private final SynchronizationManager synchronizationManager;
    private final VCacheRequestContextManager requestContextManager;
    private final VCacheFlusher flusher;

    public VCacheFactoryBean(CacheFactory cacheFactory, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, SynchronizationManager synchronizationManager, VCacheRequestContextManager requestContextManager, VCacheFlusher flusher) {
        this.requestContextManager = Objects.requireNonNull(requestContextManager);
        this.synchronizationManager = Objects.requireNonNull(synchronizationManager);
        this.cacheFactory = Objects.requireNonNull(cacheFactory);
        this.cacheSettingsDefaultsProvider = Objects.requireNonNull(cacheSettingsDefaultsProvider);
        this.flusher = Objects.requireNonNull(flusher);
    }

    public VCacheFactory getObject() throws Exception {
        AtomicReference<LegacyVCacheService> lifecycleManagerRef = new AtomicReference<LegacyVCacheService>();
        VCacheTransactionSyncHandler txSyncHandler = new VCacheTransactionSyncHandler(this.synchronizationManager, () -> (VCacheLifecycleManager)lifecycleManagerRef.get());
        LegacyVCacheService cacheService = this.createVCacheService(txSyncHandler, this.requestContextSupplier(txSyncHandler));
        lifecycleManagerRef.set(cacheService);
        return (VCacheFactory)this.flusher.wrap(cacheService);
    }

    private Supplier<RequestContext> requestContextSupplier(VCacheTransactionSyncHandler txSyncHandler) {
        return () -> this.requestContextManager.getCurrentRequestContext(requestContext -> txSyncHandler.onCleanUp((RequestContext)requestContext));
    }

    private LegacyVCacheService createVCacheService(BegunTransactionalActivityHandler txSyncHandler, Supplier<RequestContext> requestContextSupplier) {
        return new LegacyVCacheService(PRODUCT_IDENTIFIER, requestContextSupplier, requestContextSupplier, (VCacheSettingsDefaultsProvider)new VCacheSettingsProvider(this.cacheSettingsDefaultsProvider), (VCacheCreationHandler)this.cacheCreationHandler(), (MetricsCollector)new SamplingMetricsCollector(requestContextSupplier, (Predicate)new CollectMetricsPredicate()), () -> this.cacheFactory, this.legacyServiceSettings(), txSyncHandler);
    }

    private LegacyServiceSettings legacyServiceSettings() {
        return new LegacyServiceSettingsBuilder().enableSerializationHack().build();
    }

    private DefaultVCacheCreationHandler cacheCreationHandler() {
        return new DefaultVCacheCreationHandler(10000, Duration.ofHours(2L), 10000, ChangeRate.HIGH_CHANGE, ChangeRate.HIGH_CHANGE);
    }

    public Class getObjectType() {
        return VCacheFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

