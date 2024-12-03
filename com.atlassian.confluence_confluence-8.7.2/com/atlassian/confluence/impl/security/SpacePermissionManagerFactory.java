/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.impl.security.CachingSpacePermissionManager;
import com.atlassian.confluence.impl.security.CoarseGrainedCachingSpacePermissionManager;
import com.atlassian.confluence.impl.security.SpacePermissionCachePrimer;
import com.atlassian.confluence.internal.security.SpacePermissionManagerInternal;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.DefaultConfluenceAccessManager;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.concurrent.ResettableLazyReference;
import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Proxy;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class SpacePermissionManagerFactory
implements FactoryBean<SpacePermissionManagerInternal> {
    private static final Logger log = LoggerFactory.getLogger(SpacePermissionCachePrimer.class);
    private static final String PREFIX = "confluence.cache.all.space.permissions";
    private final EventListenerRegistrar eventListenerRegistrar;
    private final ResettableLazyReference<SpacePermissionManagerInternal> supplier;
    private final SpacePermissionManagerInternal proxy;
    private final PlatformTransactionManager platformTransactionManager;

    public SpacePermissionManagerFactory(EventListenerRegistrar eventListenerRegistrar, final CoarseGrainedCachingSpacePermissionManager coarseGrainedCachingSpacePermissionManager, final CachingSpacePermissionManager cachingSpacePermissionManager, final DarkFeaturesManager darkFeaturesManager, PlatformTransactionManager platformTransactionManager) {
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.platformTransactionManager = platformTransactionManager;
        this.supplier = new ResettableLazyReference<SpacePermissionManagerInternal>(){

            protected SpacePermissionManagerInternal create() throws Exception {
                if (ContainerManager.isContainerSetup() && SpacePermissionManagerFactory.useCoarseGrainedCaching(darkFeaturesManager)) {
                    log.debug("Switching to {}", coarseGrainedCachingSpacePermissionManager.getClass());
                    return coarseGrainedCachingSpacePermissionManager;
                }
                log.debug("Switching to {}", cachingSpacePermissionManager.getClass());
                return cachingSpacePermissionManager;
            }
        };
        this.proxy = (SpacePermissionManagerInternal)Proxy.newProxyInstance(SpacePermissionManagerInternal.class.getClassLoader(), new Class[]{SpacePermissionManagerInternal.class, DefaultConfluenceAccessManager.AccessManagerPermissionChecker.class}, (proxyInstance, method, args) -> method.invoke((Object)this.getCurrentSpacePermissionManager(), args));
    }

    @VisibleForTesting
    SpacePermissionManagerInternal getCurrentSpacePermissionManager() {
        return (SpacePermissionManagerInternal)this.supplier.get();
    }

    @PostConstruct
    public void registerForEvents() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregisterForEvents() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onSiteDarkFeatureEnabledEvent(SiteDarkFeatureEnabledEvent event) {
        this.onSiteDarkFeatureChange(event.getFeatureKey());
    }

    @EventListener
    public void onSiteDarkFeatureDisabledEvent(SiteDarkFeatureDisabledEvent event) {
        this.onSiteDarkFeatureChange(event.getFeatureKey());
    }

    private void onSiteDarkFeatureChange(String featureKey) {
        if (featureKey.startsWith(PREFIX)) {
            this.reset();
        }
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        this.reset();
    }

    private void reset() {
        SpacePermissionManager oldInstance = (SpacePermissionManager)this.supplier.get();
        this.flushCaches(oldInstance);
        this.supplier.reset();
    }

    private void flushCaches(SpacePermissionManager spacePermissionManager) {
        TransactionTemplate tt = new TransactionTemplate(this.platformTransactionManager);
        tt.setPropagationBehavior(0);
        tt.setReadOnly(true);
        tt.execute(status -> {
            spacePermissionManager.flushCaches();
            return null;
        });
    }

    public SpacePermissionManagerInternal getObject() throws Exception {
        return this.proxy;
    }

    public Class getObjectType() {
        return SpacePermissionManagerInternal.class;
    }

    public boolean isSingleton() {
        return true;
    }

    static boolean useCoarseGrainedCaching(DarkFeaturesManager darkFeaturesManager) {
        DarkFeatures siteDarkFeatures = darkFeaturesManager.getSiteDarkFeatures();
        return siteDarkFeatures.isFeatureEnabled("confluence.cache.all.space.permissions.enabled");
    }
}

