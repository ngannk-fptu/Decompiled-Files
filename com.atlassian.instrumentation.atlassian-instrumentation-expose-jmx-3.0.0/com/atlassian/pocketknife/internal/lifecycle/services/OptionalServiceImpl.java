/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.pocketknife.internal.lifecycle.services;

import com.atlassian.pocketknife.api.lifecycle.services.OptionalService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

@ThreadSafe
public class OptionalServiceImpl<T>
implements OptionalService<T> {
    private final BundleContext bundleContext;
    private final List<ServiceReference> serviceReferences;
    private final List<T> services;
    private final String serviceName;
    private boolean closed;

    public OptionalServiceImpl(BundleContext bundleContext, String serviceName, Filter filter) {
        this(bundleContext, serviceName, OptionalServiceImpl.getServiceReferences(bundleContext, serviceName, filter));
    }

    private static ServiceReference[] getServiceReferences(BundleContext bundleContext, String serviceName, Filter filter) {
        try {
            String filterString = filter != null ? filter.toString() : null;
            ServiceReference[] serviceRefs = bundleContext.getServiceReferences(serviceName, filterString);
            return serviceRefs == null ? new ServiceReference[]{} : serviceRefs;
        }
        catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @VisibleForTesting
    OptionalServiceImpl(BundleContext bundleContext, String serviceName, ServiceReference[] serviceReferences) {
        this.bundleContext = bundleContext;
        this.serviceName = serviceName;
        this.serviceReferences = Lists.newArrayList((Object[])serviceReferences);
        this.closed = false;
        this.services = new ArrayList<T>(this.serviceReferences.size());
        for (ServiceReference serviceReference : serviceReferences) {
            Object service;
            if (serviceReference == null || (service = bundleContext.getService(serviceReference)) == null) continue;
            this.services.add(service);
        }
    }

    private void stateCheck() {
        if (!this.isAvailable()) {
            throw new IllegalStateException("You have called on get() without checking that the service is in fact available!");
        }
    }

    @Override
    public synchronized boolean isAvailable() {
        return !this.closed && !this.services.isEmpty();
    }

    @Override
    public synchronized T get() {
        this.stateCheck();
        return this.services.get(0);
    }

    @Override
    public synchronized List<T> getAll() {
        this.stateCheck();
        return ImmutableList.copyOf(this.services);
    }

    @Override
    public synchronized void close() {
        if (!this.closed) {
            this.closed = true;
            this.services.clear();
            Throwable t = null;
            for (ServiceReference serviceReference : this.serviceReferences) {
                try {
                    if (serviceReference == null) continue;
                    this.bundleContext.ungetService(serviceReference);
                }
                catch (Throwable thrown) {
                    if (t == null) {
                        t = thrown;
                        continue;
                    }
                    t.addSuppressed(thrown);
                }
            }
            if (t != null) {
                Throwables.propagateIfPossible(t);
                throw new RuntimeException("Unable to unregister OSGi service references", t);
            }
        }
    }

    public String toString() {
        return String.format("%s : %s", this.serviceName, this.isAvailable() ? "available" : "not available");
    }
}

