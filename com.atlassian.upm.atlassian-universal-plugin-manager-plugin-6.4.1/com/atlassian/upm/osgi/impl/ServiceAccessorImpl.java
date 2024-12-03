/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.ServiceAccessor;
import com.atlassian.upm.osgi.impl.ServiceImpl;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public final class ServiceAccessorImpl
implements ServiceAccessor {
    private final PackageAccessor packageAccessor;
    private final BundleContext bundleContext;

    public ServiceAccessorImpl(PackageAccessor packageAccessor, BundleContext bundleContext) {
        this.packageAccessor = packageAccessor;
        this.bundleContext = bundleContext;
    }

    @Override
    public Iterable<Service> getServices() {
        try {
            return ServiceImpl.wrap(this.packageAccessor).fromArray((ServiceReference[])this.bundleContext.getAllServiceReferences(null, null));
        }
        catch (InvalidSyntaxException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public Service getService(long serviceId) {
        try {
            String filter = "(service.id=" + serviceId + ")";
            ServiceReference[] refs = this.bundleContext.getAllServiceReferences(null, filter);
            if (refs == null || refs.length != 1) {
                return null;
            }
            return ServiceImpl.wrap(this.packageAccessor).fromSingleton(refs[0]);
        }
        catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

