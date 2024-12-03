/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.troubleshooting.confluence.jfr;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.jfr.config.JfrServiceProductSupport;
import com.atlassian.troubleshooting.jfr.event.JfrFeatureFlagStateChangedEvent;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfluenceJfrServiceProductSupport
implements JfrServiceProductSupport,
LifecycleAware {
    private static final long SUPPORTED_BUILD_NUMBER = 11908L;
    private static final long JFR_RUNNING_BY_DEFAULT_BUILD = 18200L;
    private final EventPublisher eventPublisher;
    private final DarkFeatureManager darkFeatureManager;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public ConfluenceJfrServiceProductSupport(EventPublisher eventPublisher, DarkFeatureManager darkFeatureManager, ApplicationProperties applicationProperties) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
    }

    public void onStart() {
        this.eventPublisher.register((Object)this);
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public boolean isSupported() {
        ImmutableSet featureKeys = this.darkFeatureManager.getFeaturesEnabledForCurrentUser().getFeatureKeys();
        if (featureKeys.contains("com.atlassian.troubleshooting.jfr.disabled")) {
            return false;
        }
        if (featureKeys.contains("com.atlassian.troubleshooting.jfr.enabled")) {
            return true;
        }
        return this.isActivatedByDefault();
    }

    @Override
    public boolean isRunningByDefault() {
        return Long.parseLong(this.applicationProperties.getBuildNumber()) >= 18200L;
    }

    private boolean isActivatedByDefault() {
        return Long.parseLong(this.applicationProperties.getBuildNumber()) >= 11908L;
    }

    @EventListener
    public void onFeatureDisabledEvent(SiteDarkFeatureDisabledEvent event) {
        if (Objects.equals(event.getFeatureKey(), "com.atlassian.troubleshooting.jfr.enabled") && !this.isSupported()) {
            this.eventPublisher.publish((Object)new JfrFeatureFlagStateChangedEvent(false));
        }
    }

    @EventListener
    public void onFeatureEnabledEvent(SiteDarkFeatureEnabledEvent event) {
        if (Objects.equals(event.getFeatureKey(), "com.atlassian.troubleshooting.jfr.disabled")) {
            this.eventPublisher.publish((Object)new JfrFeatureFlagStateChangedEvent(false));
        }
    }
}

