/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.client.api.browser.BrowserEvent
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.checks.http;

import com.atlassian.analytics.client.api.browser.BrowserEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.troubleshooting.healthcheck.checks.http.NetworkPerformanceStatisticsService;
import com.atlassian.troubleshooting.healthcheck.checks.http.ProtocolsEvent;
import com.atlassian.troubleshooting.healthcheck.checks.http.ProtocolsEventProvider;
import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserEventListener {
    static final String PROTOCOLS_EVENT_NAME = "atst.healthcheck.sensors.page-protocols";
    private static final Logger LOG = LoggerFactory.getLogger(BrowserEventListener.class);
    private final Clock clock;
    private final ProtocolsEventProvider protocolsEventConsumer;
    private final NetworkPerformanceStatisticsService networkPerformanceStatisticsService;

    public BrowserEventListener(Clock clock, ProtocolsEventProvider protocolsEventConsumer, NetworkPerformanceStatisticsService networkPerformanceStatisticsService) {
        this.clock = clock;
        this.protocolsEventConsumer = protocolsEventConsumer;
        this.networkPerformanceStatisticsService = networkPerformanceStatisticsService;
    }

    @EventListener
    public void onEvent(BrowserEvent browserEvent) {
        if (BrowserEventListener.isProtocolsEvent(browserEvent)) {
            LOG.trace("Received protocols event '{}' with properties {}", (Object)browserEvent.getName(), (Object)browserEvent.getProperties());
            ProtocolsEvent protocolsEvent = new ProtocolsEvent(this.clock.millis(), browserEvent);
            this.protocolsEventConsumer.accept(protocolsEvent);
        } else {
            LOG.trace("Received non-protocols event '{}' with properties {}", (Object)browserEvent.getName(), (Object)browserEvent.getProperties());
            this.networkPerformanceStatisticsService.accept(browserEvent.getName(), browserEvent.getProperties());
        }
    }

    private static boolean isProtocolsEvent(BrowserEvent event) {
        return PROTOCOLS_EVENT_NAME.equals(event.getName());
    }
}

