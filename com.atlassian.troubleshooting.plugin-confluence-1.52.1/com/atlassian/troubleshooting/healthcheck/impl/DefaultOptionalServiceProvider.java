/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.troubleshooting.api.healthcheck.OptionalServiceProvider;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultOptionalServiceProvider
implements OptionalServiceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOptionalServiceProvider.class);
    private final BundleContext bundleContext;

    @Autowired
    public DefaultOptionalServiceProvider(@Nonnull BundleContext bundleContext) {
        this.bundleContext = Objects.requireNonNull(bundleContext);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Optional<Object> get(@Nonnull String serviceClass) {
        try (ServiceTracker tracker = null;){
            tracker = this.newServiceTracker(serviceClass);
            Optional<Object> optional = Optional.ofNullable(tracker.getService());
            return optional;
        }
    }

    @VisibleForTesting
    ServiceTracker newServiceTracker(String serviceClass) {
        ServiceTracker tracker = new ServiceTracker(this.bundleContext, serviceClass, null);
        tracker.open();
        return tracker;
    }
}

