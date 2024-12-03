/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.webresource.cdn.CDNStrategy
 *  com.atlassian.plugin.webresource.cdn.CdnStrategyProvider
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  com.google.common.base.Supplier
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.webresource;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.impl.osgi.OsgiNoServiceAvailableException;
import com.atlassian.confluence.internal.license.EnterpriseFeatureFlag;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.plugin.webresource.cdn.CdnStrategyProvider;
import com.atlassian.util.concurrent.ResettableLazyReference;
import com.google.common.base.Supplier;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ConfluenceCDNStrategyFactory
implements Supplier<CDNStrategy> {
    public static final String ATLASSIAN_CDN_FEATURE_FLAG = "atlassian.cdn.static.assets";
    private final EventPublisher eventPublisher;
    private final CdnStrategyProvider cdnStrategyProvider;
    private final EnterpriseFeatureFlag enterpriseFeatureFlag;
    private final AtomicBoolean isFinishedPluginFrameworkStartup;
    private final ResettableLazyReference<Boolean> isCdnFunctionalityAvailable;

    public ConfluenceCDNStrategyFactory(final DarkFeaturesManager darkFeaturesManager, EventPublisher eventPublisher, CdnStrategyProvider cdnStrategyProvider, EnterpriseFeatureFlag enterpriseFeatureFlag) {
        this.eventPublisher = eventPublisher;
        this.cdnStrategyProvider = cdnStrategyProvider;
        this.enterpriseFeatureFlag = enterpriseFeatureFlag;
        this.isFinishedPluginFrameworkStartup = new AtomicBoolean();
        this.isCdnFunctionalityAvailable = new ResettableLazyReference<Boolean>(){

            protected Boolean create() throws Exception {
                return ConfluenceCDNStrategyFactory.this.isFinishedPluginFrameworkStartup.get() && darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled(ConfluenceCDNStrategyFactory.ATLASSIAN_CDN_FEATURE_FLAG);
            }
        };
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public CDNStrategy get() {
        if (((Boolean)this.isCdnFunctionalityAvailable.get()).booleanValue() && this.enterpriseFeatureFlag.isEnabled()) {
            try {
                return this.cdnStrategyProvider.getCdnStrategy().orElse(null);
            }
            catch (OsgiNoServiceAvailableException e) {
                return null;
            }
        }
        return null;
    }

    @EventListener
    public void onPluginFrameworkStarted(PluginFrameworkStartedEvent event) {
        this.isFinishedPluginFrameworkStartup.set(true);
        this.isCdnFunctionalityAvailable.reset();
    }

    @EventListener
    public void onSiteDarkFeatureEnabled(SiteDarkFeatureEnabledEvent event) {
        this.isCdnFunctionalityAvailable.reset();
    }

    @EventListener
    public void onSiteDarkFeatureDisabled(SiteDarkFeatureDisabledEvent event) {
        this.isCdnFunctionalityAvailable.reset();
    }

    @EventListener
    public void onRemoteSiteDarkFeatureEvent(ClusterEventWrapper wrappedEvent) {
        Event event = wrappedEvent.getEvent();
        if (event instanceof SiteDarkFeatureEnabledEvent || event instanceof SiteDarkFeatureDisabledEvent) {
            this.isCdnFunctionalityAvailable.reset();
        }
    }
}

