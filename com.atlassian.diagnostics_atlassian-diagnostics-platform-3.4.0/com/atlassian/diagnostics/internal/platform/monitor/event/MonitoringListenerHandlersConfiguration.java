/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.util.CallingBundleResolver
 *  com.atlassian.event.config.ListenerHandlersConfiguration
 *  com.atlassian.event.spi.ListenerHandler
 */
package com.atlassian.diagnostics.internal.platform.monitor.event;

import com.atlassian.diagnostics.internal.platform.monitor.event.EventSystemMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.event.MonitoringListenerHandler;
import com.atlassian.diagnostics.util.CallingBundleResolver;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.spi.ListenerHandler;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MonitoringListenerHandlersConfiguration
implements ListenerHandlersConfiguration {
    private final CallingBundleResolver callingBundleResolver;
    private final ListenerHandlersConfiguration delegate;
    private final EventSystemMonitor monitor;
    private final String systemVersion;

    public MonitoringListenerHandlersConfiguration(CallingBundleResolver callingBundleResolver, ListenerHandlersConfiguration delegate, EventSystemMonitor monitor, String systemVersion) {
        this.callingBundleResolver = callingBundleResolver;
        this.delegate = delegate;
        this.monitor = monitor;
        this.systemVersion = systemVersion;
    }

    public List<ListenerHandler> getListenerHandlers() {
        return this.delegate.getListenerHandlers().stream().map(handler -> new MonitoringListenerHandler(this.callingBundleResolver, (ListenerHandler)handler, this.monitor, this.systemVersion)).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }
}

