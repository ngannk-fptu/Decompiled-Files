/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.spi.EventDispatcher
 *  com.atlassian.event.spi.ListenerInvoker
 */
package com.atlassian.diagnostics.internal.platform.monitor.event;

import com.atlassian.diagnostics.internal.platform.monitor.event.EventSystemMonitor;
import com.atlassian.event.spi.EventDispatcher;
import com.atlassian.event.spi.ListenerInvoker;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Supplier;

public class MonitoredEventDispatcher
implements EventDispatcher {
    private final EventDispatcher delegate;
    private final EventSystemMonitor monitor;
    private final Supplier<Integer> queueLengthSupplier;

    public MonitoredEventDispatcher(EventDispatcher delegate, EventSystemMonitor monitor, int queueLength) {
        this(delegate, monitor, () -> queueLength);
    }

    public MonitoredEventDispatcher(EventDispatcher delegate, EventSystemMonitor monitor, Supplier<Integer> queueLengthSupplier) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.monitor = Objects.requireNonNull(monitor, "monitor");
        this.queueLengthSupplier = Objects.requireNonNull(queueLengthSupplier, "queueLengthSupplier");
    }

    public void dispatch(ListenerInvoker listenerInvoker, Object o) {
        try {
            this.delegate.dispatch(listenerInvoker, o);
        }
        catch (RejectedExecutionException e) {
            this.monitor.alertEventDropped(Instant.now(), this.queueLengthSupplier.get(), o.getClass());
            throw e;
        }
    }
}

