/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent
 *  com.atlassian.confluence.setup.settings.DarkFeatures
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.ratelimiting.internal.confluence.featureflag;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.ratelimiting.internal.featureflag.DefaultRateLimitingFeatureFlagService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ConfluenceRateLimitingFeatureFlagService
extends DefaultRateLimitingFeatureFlagService {
    private final EventPublisher eventPublisher;

    public ConfluenceRateLimitingFeatureFlagService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean getDryRunEnabled() {
        return DarkFeatures.isDarkFeatureEnabled((String)"com.atlassian.ratelimiting.dry.run");
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void featureFlagEnabled(SiteDarkFeatureEnabledEvent featureEnabledEvent) {
        if (this.isDryRunKey(featureEnabledEvent.getFeatureKey())) {
            this.resetDryRunFeatureFlag();
        }
    }

    @EventListener
    public void featureFlagDisabled(SiteDarkFeatureDisabledEvent featureDisabledEvent) {
        if (this.isDryRunKey(featureDisabledEvent.getFeatureKey())) {
            this.resetDryRunFeatureFlag();
        }
    }
}

