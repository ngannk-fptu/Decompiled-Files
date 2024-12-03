/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.upm.api.util.Option
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.license.compatibility;

import com.atlassian.upm.api.util.Option;
import com.google.common.base.Preconditions;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;

public abstract class OptionalService<T>
implements DisposableBean {
    private final Class<T> type;
    private final BundleContext bundleContext;
    private final ServiceTracker tracker;

    public OptionalService(Class<T> type, BundleContext bundleContext) {
        this.type = (Class)Preconditions.checkNotNull(type, (Object)"type");
        this.bundleContext = bundleContext;
        this.tracker = new ServiceTracker(bundleContext, type.getName(), null);
        this.tracker.open();
    }

    protected final Option<T> getService() {
        return Option.option(this.type.cast(this.tracker.getService()));
    }

    public final void destroy() {
        this.tracker.close();
    }

    protected BundleContext getBundleContext() {
        return this.bundleContext;
    }
}

