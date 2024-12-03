/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class EventListeningDarkFeatureSetting {
    private static final Logger log = LoggerFactory.getLogger(EventListeningDarkFeatureSetting.class);
    private final DarkFeaturesManager darkFeaturesManager;
    private final EventPublisher eventPublisher;
    private final String darkFeatureName;
    private final AtomicBoolean enabled = new AtomicBoolean();

    public EventListeningDarkFeatureSetting(EventPublisher eventPublisher, DarkFeaturesManager darkFeaturesManager, String darkFeatureName) {
        this.eventPublisher = eventPublisher;
        this.darkFeaturesManager = darkFeaturesManager;
        this.darkFeatureName = darkFeatureName;
    }

    @PostConstruct
    public void init() {
        this.setEnabled(false);
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public boolean isEnabled() {
        return this.enabled.get();
    }

    @EventListener
    public void onFeatureEnabled(SiteDarkFeatureEnabledEvent event) {
        if (this.darkFeatureName.equals(event.getFeatureKey())) {
            this.setEnabled(true);
            log.debug("dark feature '{}' was disabled", (Object)this.darkFeatureName);
        }
    }

    @EventListener
    public void onFeatureDisabled(SiteDarkFeatureDisabledEvent event) {
        if (this.darkFeatureName.equals(event.getFeatureKey())) {
            this.setEnabled(false);
            log.debug("dark feature '{}' was enabled", (Object)this.darkFeatureName);
        }
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrappedEvent) {
        Event event = wrappedEvent.getEvent();
        if (event instanceof SiteDarkFeatureEnabledEvent) {
            this.onFeatureEnabled((SiteDarkFeatureEnabledEvent)event);
        } else if (event instanceof SiteDarkFeatureDisabledEvent) {
            this.onFeatureDisabled((SiteDarkFeatureDisabledEvent)event);
        }
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) {
        this.setEnabled(this.darkFeaturesManager.getDarkFeaturesAllUsers().isFeatureEnabled(this.darkFeatureName));
        log.debug("dark feature '{}' value is set to {}", (Object)this.darkFeatureName, (Object)this.enabled.get());
    }

    protected void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }
}

