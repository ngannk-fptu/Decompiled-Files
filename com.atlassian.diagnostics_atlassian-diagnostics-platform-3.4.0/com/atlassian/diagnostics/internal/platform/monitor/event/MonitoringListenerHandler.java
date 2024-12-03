/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.AlertTrigger$Builder
 *  com.atlassian.diagnostics.util.CallingBundleResolver
 *  com.atlassian.event.spi.ListenerHandler
 *  com.atlassian.event.spi.ListenerInvoker
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.FrameworkUtil
 */
package com.atlassian.diagnostics.internal.platform.monitor.event;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.internal.platform.monitor.event.EventSystemMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.event.MonitoredListenerInvoker;
import com.atlassian.diagnostics.util.CallingBundleResolver;
import com.atlassian.event.spi.ListenerHandler;
import com.atlassian.event.spi.ListenerInvoker;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

class MonitoringListenerHandler
implements ListenerHandler {
    private final CallingBundleResolver callingBundleResolver;
    private final ListenerHandler delegate;
    private final EventSystemMonitor monitor;
    private final String systemVersion;

    MonitoringListenerHandler(CallingBundleResolver callingBundleResolver, ListenerHandler delegate, EventSystemMonitor monitor, String systemVersion) {
        this.callingBundleResolver = callingBundleResolver;
        this.delegate = delegate;
        this.monitor = monitor;
        this.systemVersion = systemVersion;
    }

    public List<ListenerInvoker> getInvokers(Object listener) {
        if (listener == null) {
            return Collections.emptyList();
        }
        List invokers = this.delegate.getInvokers(listener);
        if (invokers == null) {
            return null;
        }
        AlertTrigger trigger = this.getTrigger(listener);
        return invokers.stream().map(invoker -> new MonitoredListenerInvoker(this.monitor, trigger, (ListenerInvoker)invoker)).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private AlertTrigger getTrigger(Object listener) {
        AlertTrigger.Builder builder = new AlertTrigger.Builder().module(listener.getClass().getName());
        Bundle bundle = this.callingBundleResolver.getCallingBundle().orElseGet(() -> FrameworkUtil.getBundle(listener.getClass()));
        if (bundle != null && bundle.getBundleId() != 0L) {
            builder.plugin(OsgiHeaderUtil.getPluginKey((Bundle)bundle), bundle.getVersion().toString());
        } else {
            builder.plugin("System", this.systemVersion);
        }
        return builder.build();
    }
}

