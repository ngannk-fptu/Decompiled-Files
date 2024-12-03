/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugins.rest.module.OsgiFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OsgiServiceAccessor<S> {
    private static final String FILTER = "(|(plugin=com.atlassian.plugins.rest)(Bundle-SymbolicName=%s))";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Class<S> serviceType;
    private final BundleContext bundleContext;
    private ServiceReference[] references;
    private final OsgiFactory<? extends S> factory;

    OsgiServiceAccessor(Class<S> serviceType, BundleContext bundleContext, OsgiFactory<? extends S> factory) {
        this.serviceType = Objects.requireNonNull(serviceType, "serviceType can't be null");
        this.bundleContext = Objects.requireNonNull(bundleContext, "bundleContext can't be null");
        this.factory = Objects.requireNonNull(factory, "factory can't be null");
    }

    Collection<? extends S> get() {
        try {
            this.references = this.bundleContext.getServiceReferences(this.serviceType.getName(), this.createFilterString(this.bundleContext.getBundle()));
            if (this.references == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(Arrays.stream(this.references).map(s -> this.factory.getInstance(this.bundleContext, (ServiceReference)s)).collect(Collectors.toList()));
        }
        catch (InvalidSyntaxException e) {
            this.logger.error("Could not get service references", (Throwable)e);
            return Collections.emptyList();
        }
    }

    void release() {
        if (this.references != null) {
            for (ServiceReference reference : this.references) {
                this.bundleContext.ungetService(reference);
            }
        }
    }

    private String createFilterString(Bundle currentBundle) {
        return String.format(FILTER, currentBundle.getSymbolicName());
    }
}

