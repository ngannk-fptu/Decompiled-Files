/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.ratelimiting.internal.analytics;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.ratelimiting.events.RateLimitingDisabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingDryRunEnabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingEnabledEvent;
import com.atlassian.ratelimiting.events.SystemRateLimitSettingsModifiedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsCreatedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsDeletedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsModifiedEvent;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsGlobalSettingsModifiedEvent;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsRateLimitDisabledEvent;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsRateLimitDryRunEnabledEvent;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsRateLimitEnabledEvent;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsUserSettingsCreatedEvent;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsUserSettingsDeletedEvent;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsUserSettingsModifiedEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class AnalyticListener
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;

    public AnalyticListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onRateLimitingDisabled(RateLimitingDisabledEvent event) {
        this.eventPublisher.publish((Object)new AnalyticsRateLimitDisabledEvent());
    }

    @EventListener
    public void onRateLimitingEnabled(RateLimitingEnabledEvent event) {
        this.eventPublisher.publish((Object)new AnalyticsRateLimitEnabledEvent());
    }

    @EventListener
    public void onRateLimitingDryRunEnabled(RateLimitingDryRunEnabledEvent event) {
        this.eventPublisher.publish((Object)new AnalyticsRateLimitDryRunEnabledEvent());
    }

    @EventListener
    public void onSystemSettingsModified(SystemRateLimitSettingsModifiedEvent event) {
        this.eventPublisher.publish((Object)new AnalyticsGlobalSettingsModifiedEvent(event.getOldSettings(), event.getNewSettings()));
    }

    @EventListener
    public void onUserSettingsCreated(UserRateLimitSettingsCreatedEvent event) {
        this.eventPublisher.publish((Object)new AnalyticsUserSettingsCreatedEvent(event.getCreatedSettings()));
    }

    @EventListener
    public void onUserSettingsDeleted(UserRateLimitSettingsDeletedEvent event) {
        this.eventPublisher.publish((Object)new AnalyticsUserSettingsDeletedEvent(event.getSettings()));
    }

    @EventListener
    public void onUserSettingsModified(UserRateLimitSettingsModifiedEvent event) {
        this.eventPublisher.publish((Object)new AnalyticsUserSettingsModifiedEvent(event.getOldSettings(), event.getNewSettings()));
    }
}

