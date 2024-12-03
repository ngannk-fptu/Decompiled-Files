/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.NotificationException
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginTransactionEndEvent
 *  com.atlassian.plugin.event.events.PluginTransactionStartEvent
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.event.NotificationException;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginTransactionEndEvent;
import com.atlassian.plugin.event.events.PluginTransactionStartEvent;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginTransactionContext {
    private static final Logger log = LoggerFactory.getLogger(PluginTransactionContext.class);
    private static ThreadLocal<AtomicInteger> level = ThreadLocal.withInitial(() -> new AtomicInteger(0));
    private static ThreadLocal<List<Object>> events = ThreadLocal.withInitial(ArrayList::new);
    private final PluginEventManager pluginEventManager;

    public PluginTransactionContext(PluginEventManager pluginEventManager) {
        this.pluginEventManager = pluginEventManager;
    }

    void start() {
        if (level.get().getAndIncrement() == 0) {
            if (log.isTraceEnabled()) {
                log.trace("Starting plugin event transaction.", new Throwable());
            }
            this.broadcastIgnoreError(new PluginTransactionStartEvent());
        }
    }

    public void addEvent(Object event) {
        if (level.get().get() > 0) {
            events.get().add(event);
        }
    }

    void stop() {
        if (level.get().decrementAndGet() == 0) {
            if (log.isTraceEnabled()) {
                log.trace("Stopping plugin event transaction.", new Throwable());
            }
            List<Object> eventsInTransaction = events.get();
            events.remove();
            this.broadcastIgnoreError(new PluginTransactionEndEvent(eventsInTransaction));
        }
    }

    public void wrap(Runnable runnable) {
        this.start();
        try {
            runnable.run();
        }
        finally {
            this.stop();
        }
    }

    public <T> T wrap(Supplier<T> supplier) {
        this.start();
        try {
            T t = supplier.get();
            return t;
        }
        finally {
            this.stop();
        }
    }

    private void broadcastIgnoreError(Object event) {
        try {
            this.pluginEventManager.broadcast(event);
        }
        catch (NotificationException e) {
            log.warn("Error broadcasting '{}': {}. Continuing anyway.", new Object[]{event, e, e});
        }
    }

    int getLevel() {
        return level.get().get();
    }

    List<Object> getEvents() {
        return ImmutableList.copyOf((Collection)events.get());
    }
}

