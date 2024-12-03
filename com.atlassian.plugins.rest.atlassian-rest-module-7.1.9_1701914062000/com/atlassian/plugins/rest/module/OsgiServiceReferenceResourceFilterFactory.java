/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.plugins.rest.module;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

class OsgiServiceReferenceResourceFilterFactory
implements ResourceFilterFactory {
    private final BundleContext bundleContext;
    private final ServiceReference serviceReference;

    OsgiServiceReferenceResourceFilterFactory(BundleContext bundleContext, ServiceReference serviceReference) {
        this.bundleContext = Objects.requireNonNull(bundleContext, "bundleContext can't be null");
        this.serviceReference = Objects.requireNonNull(serviceReference, "serviceReference can't be null");
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        ResourceFilterFactory resourceFilterFactory;
        try {
            resourceFilterFactory = (ResourceFilterFactory)this.bundleContext.getService(this.serviceReference);
        }
        catch (ClassCastException e) {
            throw new IllegalStateException("The service registered should be an instance of " + ResourceFilterFactory.class, e);
        }
        return resourceFilterFactory != null ? resourceFilterFactory.create(am) : Collections.emptyList();
    }
}

