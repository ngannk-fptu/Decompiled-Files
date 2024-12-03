/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.client.api.browser.BrowserEvent
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.healthcheck.checks.http;

import com.atlassian.analytics.client.api.browser.BrowserEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

class ProtocolsEvent {
    static final String UNKNOWN_PROTOCOL = "unknown";
    static final String RESOURCE_PROPERTY = "resourceProtocols";
    static final String NAVIGATION_PROPERTY = "navigationProtocol";
    static final String USER_AGENT_PROPERTY = "userAgent";
    private final long timestamp;
    private final Optional<String> navigationProtocol;
    private final Collection<String> resourceProtocols;
    private final Optional<String> userAgent;

    ProtocolsEvent(long timestamp, BrowserEvent browserEvent) {
        this.timestamp = timestamp;
        this.resourceProtocols = ProtocolsEvent.getResourceProtocols(browserEvent.getProperties());
        this.navigationProtocol = ProtocolsEvent.getNavigationProtocol(browserEvent.getProperties());
        this.userAgent = ProtocolsEvent.getUserAgent(browserEvent.getProperties());
    }

    long getTimestamp() {
        return this.timestamp;
    }

    Collection<String> getResourceProtocols() {
        return Collections.unmodifiableCollection(this.resourceProtocols);
    }

    Optional<String> getNavigationProtocol() {
        return this.navigationProtocol;
    }

    public Optional<String> getUserAgent() {
        return this.userAgent;
    }

    boolean isAfter(ProtocolsEvent o) {
        return o == null || this.timestamp > o.timestamp;
    }

    private static Optional<String> getNavigationProtocol(Map<String, Object> properties) {
        return ProtocolsEvent.getProperty(properties, NAVIGATION_PROPERTY);
    }

    @Nonnull
    private static List<String> getResourceProtocols(Map<String, Object> properties) {
        return ProtocolsEvent.getProperty(properties, RESOURCE_PROPERTY).map(value -> Arrays.asList(value.split(","))).orElse(Collections.emptyList());
    }

    private static Optional<String> getUserAgent(Map<String, Object> properties) {
        return ProtocolsEvent.getProperty(properties, USER_AGENT_PROPERTY);
    }

    private static Optional<String> getProperty(Map<String, Object> properties, String propertyName) {
        return Optional.ofNullable(properties).map(p -> p.get(propertyName)).map(o -> (String)o);
    }

    public String toString() {
        return "ProtocolsEvent{timestamp=" + this.timestamp + ", navigationProtocol=" + this.navigationProtocol.orElse(UNKNOWN_PROTOCOL) + ", resourceProtocols=" + this.resourceProtocols + ", userAgent=" + this.userAgent + '}';
    }
}

