/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.client.api.browser.BrowserEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginEvent
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks.http;

import com.atlassian.analytics.client.api.browser.BrowserEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginEvent;
import com.atlassian.troubleshooting.healthcheck.checks.eol.ClockFactory;
import com.atlassian.troubleshooting.healthcheck.checks.http.BrowserEventListener;
import com.atlassian.troubleshooting.healthcheck.checks.http.NetworkPerformanceStatisticsService;
import com.atlassian.troubleshooting.healthcheck.checks.http.ProtocolsEventProvider;
import java.time.Clock;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;

public class BrowserEventListenerRegistrar {
    private static final Optional<BrowserEventListener> EMPTY = Optional.empty();
    private final Clock clock;
    private final EventPublisher eventPublisher;
    private final ProtocolsEventProvider protocolsEventConsumer;
    private final AtomicReference<Optional<BrowserEventListener>> browserEventListenerRef = new AtomicReference<Optional<BrowserEventListener>>(EMPTY);
    private final NetworkPerformanceStatisticsService networkPerformanceStatisticsService;

    @Autowired
    public BrowserEventListenerRegistrar(ClockFactory clockFactory, EventPublisher eventPublisher, ProtocolsEventProvider protocolsEventConsumer, NetworkPerformanceStatisticsService networkPerformanceStatisticsService) {
        this.clock = clockFactory.makeClock();
        this.eventPublisher = eventPublisher;
        this.protocolsEventConsumer = protocolsEventConsumer;
        this.networkPerformanceStatisticsService = networkPerformanceStatisticsService;
    }

    @PostConstruct
    void initialize() {
        this.eventPublisher.register((Object)this);
        this.registerBrowserEventListener();
    }

    @EventListener
    public void onEvent(PluginEnabledEvent pluginEnabledEvent) {
        if (BrowserEventListenerRegistrar.isAnalyticsClientPluginEvent((PluginEvent)pluginEnabledEvent)) {
            this.registerBrowserEventListener();
        }
    }

    private void registerBrowserEventListener() {
        try {
            BrowserEvent.class.getName();
        }
        catch (NoClassDefFoundError ignored) {
            return;
        }
        Optional<BrowserEventListener> listener = Optional.of(new BrowserEventListener(this.clock, this.protocolsEventConsumer, this.networkPerformanceStatisticsService));
        if (this.browserEventListenerRef.compareAndSet(EMPTY, listener)) {
            listener.ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).register(arg_0));
        }
    }

    @PreDestroy
    void destroy() {
        this.eventPublisher.unregister((Object)this);
        this.browserEventListenerRef.get().ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).unregister(arg_0));
    }

    private static boolean isAnalyticsClientPluginEvent(PluginEvent pluginEvent) {
        return pluginEvent.getPlugin().getKey().equals("com.atlassian.analytics.analytics-client");
    }
}

