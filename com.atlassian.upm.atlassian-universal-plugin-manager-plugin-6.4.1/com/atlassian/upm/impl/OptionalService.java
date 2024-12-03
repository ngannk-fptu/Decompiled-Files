/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.impl;

import com.atlassian.upm.api.util.Option;
import java.util.Objects;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;

public abstract class OptionalService<T>
implements DisposableBean {
    private final Class<T> type;
    private final BundleContext bundleContext;
    private final ServiceTracker tracker;

    public OptionalService(Class<T> type, BundleContext bundleContext) {
        this(type, bundleContext, new ServiceTracker(bundleContext, type.getName(), null));
    }

    protected OptionalService(Class<T> type, BundleContext bundleContext, ServiceTracker tracker) {
        this.type = Objects.requireNonNull(type, "type");
        this.bundleContext = bundleContext;
        this.tracker = tracker;
        tracker.open();
    }

    protected final Option<T> getService() {
        return Option.option(this.type.cast(this.tracker.getService()));
    }

    protected final Option<T> getService(ServiceReference sr) {
        return Option.option(this.type.cast(this.tracker.getService(sr)));
    }

    protected final Option<ServiceReference> getServiceReference() {
        return Option.option(this.tracker.getServiceReference());
    }

    public void destroy() {
        this.tracker.close();
    }

    protected BundleContext getBundleContext() {
        return this.bundleContext;
    }
}

