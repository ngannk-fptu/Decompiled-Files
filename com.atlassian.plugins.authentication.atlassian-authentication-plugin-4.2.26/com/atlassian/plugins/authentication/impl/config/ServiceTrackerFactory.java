/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 */
package com.atlassian.plugins.authentication.impl.config;

import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public interface ServiceTrackerFactory<T> {
    public ServiceTracker<T, Object> create(BundleContext var1, Class<T> var2, @Nullable ServiceTrackerCustomizer<T, Object> var3);
}

