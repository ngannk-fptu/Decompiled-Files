/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertListener
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.FrameworkUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertListener;
import com.atlassian.diagnostics.internal.AlertPublisher;
import com.atlassian.diagnostics.internal.PluginHelper;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockFreeAlertPublisher
implements AlertPublisher {
    private static final Logger log = LoggerFactory.getLogger(LockFreeAlertPublisher.class);
    private final Executor executor;
    private final ConcurrentMap<String, AlertListener> listeners;
    private final PluginHelper pluginHelper;

    public LockFreeAlertPublisher(Collection<AlertListener> coreListeners, Executor executor, PluginHelper pluginHelper) {
        this.executor = executor;
        this.pluginHelper = pluginHelper;
        this.listeners = new ConcurrentHashMap<String, AlertListener>();
        coreListeners.forEach(this::internalSubscribe);
    }

    @Override
    public void publish(@Nonnull Alert alert) {
        Objects.requireNonNull(alert, "alert");
        this.listeners.values().forEach(listener -> {
            try {
                this.executor.execute(new AlertListenerInvoker(alert, (AlertListener)listener));
            }
            catch (RejectedExecutionException e) {
                log.warn("Failed to schedule invocation of AlertListener {}", (Object)listener.getClass().getName(), (Object)e);
            }
        });
    }

    @Override
    @Nonnull
    public String subscribe(@Nonnull AlertListener listener) {
        Objects.requireNonNull(listener, "listener");
        this.validateCallerIsHostOrSystemPlugin(listener);
        return this.internalSubscribe(listener);
    }

    @Override
    public boolean unsubscribe(@Nonnull String subscriptionId) {
        return this.listeners.remove(subscriptionId) != null;
    }

    private String internalSubscribe(AlertListener listener) {
        String subscriptionId = UUID.randomUUID().toString();
        while (this.listeners.putIfAbsent(subscriptionId, listener) != null) {
            subscriptionId = UUID.randomUUID().toString();
        }
        return subscriptionId;
    }

    private void validateCallerIsHostOrSystemPlugin(AlertListener listener) {
        Bundle bundle = this.pluginHelper.getCallingBundle().orElseGet(() -> FrameworkUtil.getBundle(listener.getClass()));
        if (bundle != null && this.pluginHelper.isUserInstalled(bundle)) {
            throw new IllegalArgumentException("User installed plugins cannot register AlertListeners");
        }
    }

    private static class AlertListenerInvoker
    implements Runnable {
        private final Alert alert;
        private final AlertListener listener;

        private AlertListenerInvoker(Alert alert, AlertListener listener) {
            this.alert = alert;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                this.listener.onAlert(this.alert);
            }
            catch (Exception e) {
                log.warn("AlertListener {} failed", (Object)this.listener.getClass().getName(), (Object)e);
            }
        }
    }
}

