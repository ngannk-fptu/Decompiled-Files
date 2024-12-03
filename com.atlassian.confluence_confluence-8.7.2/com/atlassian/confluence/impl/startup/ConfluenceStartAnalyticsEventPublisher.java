/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.startup;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.impl.startup.ConfluenceStartAnalyticsEventFactory;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.confluence.util.profiling.TimedAnalytics;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceStartAnalyticsEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceStartAnalyticsEventPublisher.class);
    private final ConfluenceStartAnalyticsEventFactory confluenceStartEventFactory;
    private final EventPublisher eventPublisher;
    private final TenantRegistry tenantRegistry;
    private final Executor executor;

    public ConfluenceStartAnalyticsEventPublisher(EventPublisher eventPublisher, TenantRegistry tenantRegistry, ConfluenceStartAnalyticsEventFactory confluenceStartEventFactory, Executor executor) {
        this.tenantRegistry = Objects.requireNonNull(tenantRegistry);
        this.executor = Objects.requireNonNull(executor);
        this.confluenceStartEventFactory = Objects.requireNonNull(confluenceStartEventFactory);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @PostConstruct
    public void registerForEvents() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregisterForEvents() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        this.executor.execute(this::tryPublishEvent);
        TimedAnalytics.timedAnalytics().startAt("confluence.profiling.startup.total", Duration.ZERO).close();
    }

    private void tryPublishEvent() {
        if (this.tenantRegistry.isRegistryVacant()) {
            log.warn("Tenant registry is vacant, not sending confluence.start event");
        } else {
            try {
                log.info("Publishing confluence.start event");
                this.eventPublisher.publish((Object)this.confluenceStartEventFactory.createConfluenceStartEvent());
            }
            catch (RuntimeException ex) {
                log.warn("Failed to publish confluence.start event", (Throwable)ex);
            }
        }
    }
}

