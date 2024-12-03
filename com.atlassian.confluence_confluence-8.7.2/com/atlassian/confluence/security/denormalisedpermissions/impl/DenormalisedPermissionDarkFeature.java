/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Internal
public final class DenormalisedPermissionDarkFeature
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedPermissionDarkFeature.class);
    private static final String FEATURE_KEY = "confluence.denormalisedpermissions";
    private final EventPublisher eventPublisher;
    private final DarkFeatureManager darkFeatureManager;
    private final AtomicBoolean enabled = new AtomicBoolean();

    public DenormalisedPermissionDarkFeature(EventPublisher eventPublisher, DarkFeatureManager darkFeatureManager) {
        this.eventPublisher = eventPublisher;
        this.darkFeatureManager = darkFeatureManager;
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        if (this.darkFeatureManager.isEnabledForAllUsers(FEATURE_KEY).orElse(false).booleanValue()) {
            this.setEnabled();
        } else {
            this.setDisabled();
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onSiteDarkFeatureEnabledEvent(SiteDarkFeatureEnabledEvent event) {
        if (FEATURE_KEY.equals(event.getFeatureKey())) {
            this.setEnabled();
        }
    }

    @EventListener
    public void onSiteDarkFeatureDisabledEvent(SiteDarkFeatureDisabledEvent event) {
        if (FEATURE_KEY.equals(event.getFeatureKey())) {
            this.setDisabled();
        }
    }

    @EventListener
    public void onClusterEventWrapper(ClusterEventWrapper clusterEventWrapper) {
        Event event = clusterEventWrapper.getEvent();
        if (event instanceof SiteDarkFeatureEnabledEvent) {
            this.onSiteDarkFeatureEnabledEvent((SiteDarkFeatureEnabledEvent)event);
        } else if (event instanceof SiteDarkFeatureDisabledEvent) {
            this.onSiteDarkFeatureDisabledEvent((SiteDarkFeatureDisabledEvent)event);
        }
    }

    public boolean isEnabled() {
        return this.enabled.get();
    }

    private void setEnabled() {
        this.enabled.set(true);
        log.debug("Dark feature '{}' enabled.", (Object)FEATURE_KEY);
    }

    private void setDisabled() {
        this.enabled.set(false);
        log.debug("Dark feature '{}' disabled.", (Object)FEATURE_KEY);
    }
}

