/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.google.common.collect.Iterables
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.impl.security.SpacePermissionManagerFactory;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.google.common.collect.Iterables;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpacePermissionCachePrimer {
    private static final Logger log = LoggerFactory.getLogger(SpacePermissionCachePrimer.class);
    private final EventListenerRegistrar eventListenerRegistrar;
    private final Primeable spacePermissionManager;
    private final ScheduledExecutorService scheduledExecutorService;
    private final TenantRegistry tenantRegistry;
    private final SpaceManager spaceManager;
    private final DarkFeaturesManager darkFeaturesManager;

    public SpacePermissionCachePrimer(EventListenerRegistrar eventListenerRegistrar, Primeable spacePermissionManager, ScheduledExecutorService scheduledExecutorService, TenantRegistry tenantRegistry, SpaceManager spaceManager, DarkFeaturesManager darkFeaturesManager) {
        this.eventListenerRegistrar = Objects.requireNonNull(eventListenerRegistrar);
        this.spacePermissionManager = Objects.requireNonNull(spacePermissionManager);
        this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService);
        this.tenantRegistry = Objects.requireNonNull(tenantRegistry);
        this.spaceManager = Objects.requireNonNull(spaceManager);
        this.darkFeaturesManager = Objects.requireNonNull(darkFeaturesManager);
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
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        if (!SpacePermissionManagerFactory.useCoarseGrainedCaching(this.darkFeaturesManager)) {
            return;
        }
        long jitterMillis = Long.getLong("confluenceStartEvent.publishJitterMillis", 10000L);
        long delayMillis = (long)(Math.random() * (double)jitterMillis);
        log.info("Priming space permission cache in {} ms", (Object)delayMillis);
        this.scheduledExecutorService.schedule(this::primeCache, delayMillis, TimeUnit.MILLISECONDS);
    }

    private void primeCache() {
        if (this.tenantRegistry.isRegistryVacant()) {
            log.warn("Tenant registry is vacant, not priming space permission cache");
            return;
        }
        ListBuilder<Space> spaces = this.spaceManager.getSpaces(SpacesQuery.newQuery().withSpaceStatus(SpaceStatus.CURRENT).withSpaceType(SpaceType.GLOBAL).build());
        this.spacePermissionManager.prime(Iterables.concat(spaces));
    }

    static interface Primeable
    extends SpacePermissionManager {
        public void prime(Iterable<? extends Space> var1);
    }
}

