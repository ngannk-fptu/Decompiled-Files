/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginDisablingEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.core;

import com.atlassian.audit.core.InMemoryBufferingConsumer;
import com.atlassian.audit.core.impl.broker.AuditBroker;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisablingEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferingAuditBroker
implements AuditBroker {
    @VisibleForTesting
    static final String AUDIT_PLUGIN_KEY = "com.atlassian.audit.atlassian-audit-plugin";
    private static final Logger log = LoggerFactory.getLogger(BufferingAuditBroker.class);
    private final EventPublisher eventPublisher;
    private final Supplier<Optional<AuditBroker>> delegateBrokerSupplier;
    private final InMemoryBufferingConsumer<AuditEntity> bufferedConsumer;
    private volatile boolean pluginFrameworkStarted;
    private volatile boolean auditingPluginIsUp;

    public BufferingAuditBroker(EventPublisher eventPublisher, Supplier<Optional<AuditBroker>> delegateBrokerSupplier, int bufferLimit) {
        this.eventPublisher = eventPublisher;
        this.delegateBrokerSupplier = delegateBrokerSupplier;
        this.bufferedConsumer = new InMemoryBufferingConsumer<AuditEntity>(() -> this.realBrokerIfReady().map(broker -> broker::audit), bufferLimit, e -> log.error("Downtime buffer is full and the entity is going to be discarded. To increase the buffer size please override the system property audit.broker.downtime.buffer.size. Current buffer limit: {}. Discarded entity: {}", (Object)bufferLimit, e));
    }

    public void start() {
        this.eventPublisher.register((Object)this);
    }

    private Optional<AuditBroker> realBrokerIfReady() {
        if (this.pluginFrameworkStarted && this.auditingPluginIsUp) {
            return this.delegateBrokerSupplier.get();
        }
        return Optional.empty();
    }

    @EventListener
    public void onPluginFrameworkStarted(PluginFrameworkStartedEvent event) {
        log.trace("onPluginFrameworkStarted auditingPluginIsUp={}", (Object)this.auditingPluginIsUp);
        this.pluginFrameworkStarted = true;
        this.bufferedConsumer.tryFlushBuffer();
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (AUDIT_PLUGIN_KEY.equals(event.getPlugin().getKey())) {
            this.auditingPluginIsUp = true;
            log.trace("onPluginEnabled pluginFrameworkStarted={}", (Object)this.pluginFrameworkStarted);
            if (this.pluginFrameworkStarted) {
                this.bufferedConsumer.tryFlushBuffer();
            }
        }
    }

    @EventListener
    public void onPluginDisabling(PluginDisablingEvent event) {
        if (AUDIT_PLUGIN_KEY.equals(event.getPlugin().getKey())) {
            log.trace("onPluginDisabling");
            this.auditingPluginIsUp = false;
        }
    }

    @Override
    public void audit(@Nonnull AuditEntity entity) {
        this.bufferedConsumer.accept(entity);
    }
}

