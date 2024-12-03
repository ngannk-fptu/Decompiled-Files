/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.core.install;

import com.atlassian.upm.HandlerRegistry;
import com.atlassian.upm.api.util.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;

public abstract class AbstractConnectHandlerRegistry<T>
implements HandlerRegistry<T>,
DisposableBean {
    private final BundleContext bundleContext;
    private final Set<T> internalHandlers;
    private Option<ServiceTracker> serviceTracker = Option.none();

    public AbstractConnectHandlerRegistry(BundleContext bundleContext, Set<T> internalHandlers) {
        this.bundleContext = Objects.requireNonNull(bundleContext, "bundleContext");
        this.internalHandlers = Collections.unmodifiableSet(internalHandlers);
    }

    @Override
    public Iterable<T> getHandlers() {
        ServiceTracker tracker = this.getServiceTracker();
        ArrayList<Object> ret = new ArrayList<Object>();
        for (ServiceReference[] srs : Option.option(tracker.getServiceReferences())) {
            for (ServiceReference sr : srs) {
                if (!this.isAllowableInstallHandler(sr)) continue;
                ret.add(tracker.getService(sr));
            }
        }
        return Stream.concat(this.internalHandlers.stream(), ret.stream()).collect(Collectors.toList());
    }

    @Override
    public abstract Class<T> getHandlerClass();

    void setServiceTracker(ServiceTracker serviceTracker) {
        for (ServiceTracker t : this.serviceTracker) {
            if (t == serviceTracker) continue;
            t.close();
        }
        this.serviceTracker = Option.some(serviceTracker);
    }

    public void destroy() {
        for (ServiceTracker t : this.serviceTracker) {
            t.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ServiceTracker getServiceTracker() {
        Iterator<ServiceTracker> iterator = this.serviceTracker.iterator();
        if (iterator.hasNext()) {
            ServiceTracker st = iterator.next();
            return st;
        }
        iterator = this;
        synchronized (iterator) {
            ServiceTracker tracker = new ServiceTracker(this.bundleContext, this.getHandlerClass().getName(), null);
            tracker.open();
            this.serviceTracker = Option.some(tracker);
            return tracker;
        }
    }

    private boolean isAllowableInstallHandler(ServiceReference sr) {
        return sr.getBundle().getSymbolicName().equals("com.atlassian.plugins.atlassian-connect-plugin");
    }
}

