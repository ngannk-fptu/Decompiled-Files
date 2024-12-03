/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import org.eclipse.gemini.blueprint.service.ServiceUnavailableException;
import org.eclipse.gemini.blueprint.service.importer.ServiceProxyDestroyedException;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceInvoker;
import org.eclipse.gemini.blueprint.service.importer.support.internal.exception.BlueprintExceptionFactory;
import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;

public class ServiceStaticInterceptor
extends ServiceInvoker {
    private static final int hashCode = ServiceStaticInterceptor.class.hashCode() * 13;
    private boolean destroyed = false;
    private final Object lock = new Object();
    private final ServiceReference reference;
    private final BundleContext bundleContext;
    private boolean useBlueprintExceptions = false;
    private final Filter filter;
    private volatile Object target = null;

    public ServiceStaticInterceptor(BundleContext context, ServiceReference reference) {
        Assert.notNull((Object)context);
        Assert.notNull((Object)reference, (String)"a not null service reference is required");
        this.bundleContext = context;
        this.reference = reference;
        this.filter = OsgiFilterUtils.createFilter(OsgiFilterUtils.getFilter(reference));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object getTarget() {
        Object object = this.lock;
        synchronized (object) {
            if (this.destroyed) {
                throw new ServiceProxyDestroyedException();
            }
        }
        if (this.reference.getBundle() != null) {
            if (this.target == null) {
                object = this.lock;
                synchronized (object) {
                    if (this.target == null && !this.destroyed) {
                        this.target = this.bundleContext.getService(this.reference);
                    }
                }
            }
            return this.target;
        }
        throw this.useBlueprintExceptions ? BlueprintExceptionFactory.createServiceUnavailableException(this.filter) : new ServiceUnavailableException(this.filter);
    }

    public void setUseBlueprintExceptions(boolean useBlueprintExceptions) {
        this.useBlueprintExceptions = useBlueprintExceptions;
    }

    @Override
    public ServiceReference getServiceReference() {
        return this.reference;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void destroy() {
        Object object = this.lock;
        synchronized (object) {
            this.destroyed = true;
        }
        try {
            this.bundleContext.ungetService(this.reference);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
        this.target = null;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ServiceStaticInterceptor) {
            ServiceStaticInterceptor oth = (ServiceStaticInterceptor)other;
            return this.reference.equals(oth.reference) && this.bundleContext.equals(oth.bundleContext);
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }
}

