/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 */
package com.atlassian.audit.core;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class OsgiServiceSupplier<T, S>
implements Supplier<Optional<S>>,
ServiceTrackerCustomizer<T, T> {
    private final String className;
    private final Supplier<BundleContext> bundleContextSupplier;
    private final EventPublisher eventPublisher;
    private volatile ServiceTracker<T, T> serviceTracker;
    private final AtomicReference<S> currentReferenceOrNull = new AtomicReference();
    private final Function<T, S> osgiInstanceTransformer;

    public OsgiServiceSupplier(Supplier<BundleContext> bundleContextSupplier, EventPublisher eventPublisher, String className, Function<T, S> osgiInstanceTransformer) {
        this.bundleContextSupplier = bundleContextSupplier;
        this.eventPublisher = eventPublisher;
        this.className = className;
        this.osgiInstanceTransformer = osgiInstanceTransformer;
    }

    public void start() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onPluginFrameworkStarted(PluginFrameworkStartedEvent event) {
        BundleContext bundleContext = this.bundleContextSupplier.get();
        this.serviceTracker = new ServiceTracker(bundleContext, this.className, (ServiceTrackerCustomizer)this);
        this.serviceTracker.open();
        ServiceReference existingServiceReference = bundleContext.getServiceReference(this.className);
        if (existingServiceReference != null) {
            Object existingService = bundleContext.getService(existingServiceReference);
            this.currentReferenceOrNull.set(this.transformService(existingService));
        }
    }

    @EventListener
    public void onPluginFrameworkShuttingDown(PluginFrameworkShuttingDownEvent event) {
        if (this.serviceTracker != null) {
            this.serviceTracker.close();
        }
    }

    @Override
    public Optional<S> get() {
        return Optional.ofNullable(this.currentReferenceOrNull.get());
    }

    public T addingService(ServiceReference<T> serviceReference) {
        Object service = serviceReference.getBundle().getBundleContext().getService(serviceReference);
        this.currentReferenceOrNull.set(this.transformService(service));
        return (T)service;
    }

    public void modifiedService(ServiceReference<T> serviceReference, T service) {
    }

    public void removedService(ServiceReference<T> serviceReference, T auditCoverageConfigService) {
        this.currentReferenceOrNull.set(null);
    }

    private S transformService(T existingService) {
        if (existingService == null) {
            return null;
        }
        return this.osgiInstanceTransformer.apply(existingService);
    }
}

