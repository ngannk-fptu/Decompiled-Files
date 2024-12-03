/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.event.spi.ListenerInvoker
 */
package com.atlassian.diagnostics.internal.platform.monitor.event;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.internal.platform.monitor.event.EventSystemMonitor;
import com.atlassian.event.spi.ListenerInvoker;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class MonitoredListenerInvoker
implements ListenerInvoker {
    private final ListenerInvoker delegate;
    private final EventSystemMonitor monitor;
    private final AlertTrigger trigger;

    public MonitoredListenerInvoker(EventSystemMonitor monitor, AlertTrigger trigger, ListenerInvoker delegate) {
        this.delegate = delegate;
        this.monitor = monitor;
        this.trigger = trigger;
    }

    public Optional<String> getScope() {
        return this.delegate.getScope();
    }

    public Set<Class<?>> getSupportedEventTypes() {
        return this.delegate.getSupportedEventTypes();
    }

    public void invoke(Object event) {
        this.monitor.invokeMonitored(this.trigger, this.delegate, event);
    }

    public boolean supportAsynchronousEvents() {
        return this.delegate.supportAsynchronousEvents();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MonitoredListenerInvoker that = (MonitoredListenerInvoker)o;
        return Objects.equals(this.delegate, that.delegate);
    }

    public int hashCode() {
        return Objects.hash(this.delegate);
    }

    public String toString() {
        return this.delegate.toString() + " (monitored)";
    }
}

