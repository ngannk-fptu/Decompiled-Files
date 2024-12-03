/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 *  com.atlassian.diagnostics.internal.DefaultDiagnosticsConfiguration
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.diagnostics.internal.DefaultDiagnosticsConfiguration;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceDiagnosticsConfiguration
implements DiagnosticsConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceDiagnosticsConfiguration.class);
    private static final long ALERT_RETENTION_PERIOD_DAYS = Integer.getInteger("diagnostics.alert.retention.period.days", 30).intValue();
    private static final long ALERT_TRUNCATION_INTERVAL_MINUTES = Integer.getInteger("diagnostics.alert.truncation.interval.minutes", 30).intValue();
    private static final long THREAD_DUMP_PRODUCER_COOLDOWN_MINUTES = Integer.getInteger("diagnostics.thread.dump.producer.cooldown.minutes", 30).intValue();
    private final DiagnosticsConfiguration delegate;
    private final EventPublisher eventPublisher;
    private final AtomicBoolean active = new AtomicBoolean();

    public ConfluenceDiagnosticsConfiguration(EventPublisher eventPublisher) {
        this.delegate = new DefaultDiagnosticsConfiguration();
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
        this.active.set(false);
        log.debug("Confluence diagnostics has been deactivated");
    }

    @EventListener
    public void onApplicationStarted(ApplicationStartedEvent event) {
        log.debug("Confluence diagnostics has been activated");
        this.active.set(true);
    }

    public @NonNull Duration getAlertRetentionPeriod() {
        return Duration.ofDays(ALERT_RETENTION_PERIOD_DAYS);
    }

    public @NonNull Duration getAlertTruncationInterval() {
        return Duration.ofMinutes(ALERT_TRUNCATION_INTERVAL_MINUTES);
    }

    public @NonNull String getNodeName() {
        return this.delegate.getNodeName();
    }

    @Nonnull
    public Duration getThreadDumpProducerCooldown() {
        return Duration.ofMinutes(THREAD_DUMP_PRODUCER_COOLDOWN_MINUTES);
    }

    public boolean isEnabled() {
        return this.active.get();
    }
}

