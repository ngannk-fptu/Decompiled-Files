/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.jira.config.FeatureDisabledEvent
 *  com.atlassian.jira.config.FeatureEnabledEvent
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.ratelimiting.internal.jira.featureflag;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.config.FeatureDisabledEvent;
import com.atlassian.jira.config.FeatureEnabledEvent;
import com.atlassian.ratelimiting.internal.featureflag.DefaultRateLimitingFeatureFlagService;
import com.atlassian.sal.api.features.DarkFeatureManager;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class JiraRateLimitingFeatureFlagService
extends DefaultRateLimitingFeatureFlagService {
    private final DarkFeatureManager darkFeatureManager;
    private final EventPublisher eventPublisher;

    public JiraRateLimitingFeatureFlagService(DarkFeatureManager darkFeatureManager, EventPublisher eventPublisher) {
        this.darkFeatureManager = darkFeatureManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean getDryRunEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers("com.atlassian.ratelimiting.dry.run").orElse(false);
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
    public void featureFlagEnabled(FeatureEnabledEvent featureEnabledEvent) {
        if (this.isDryRunKey(featureEnabledEvent.feature())) {
            this.resetDryRunFeatureFlag();
        }
    }

    @EventListener
    public void featureFlagDisabled(FeatureDisabledEvent featureDisabledEvent) {
        if (this.isDryRunKey(featureDisabledEvent.feature())) {
            this.resetDryRunFeatureFlag();
        }
    }
}

