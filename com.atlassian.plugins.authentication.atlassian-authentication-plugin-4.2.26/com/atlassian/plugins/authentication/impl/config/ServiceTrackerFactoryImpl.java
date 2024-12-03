/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Named
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.plugins.authentication.impl.config.ServiceTrackerFactory;
import javax.inject.Named;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@Named
public class ServiceTrackerFactoryImpl<T>
implements ServiceTrackerFactory<T> {
    @Override
    public ServiceTracker<T, Object> create(BundleContext bundleContext, Class<T> clazz, @Nullable ServiceTrackerCustomizer<T, Object> customizer) {
        return new ServiceTracker(bundleContext, clazz, customizer);
    }
}

