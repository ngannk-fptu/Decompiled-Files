/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.osgi.context.BundleContextAware
 */
package com.atlassian.plugin.notifications.spi;

import com.google.common.base.Preconditions;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.osgi.context.BundleContextAware;

public abstract class OptionalService<T>
implements BundleContextAware,
DisposableBean {
    private final Class<T> type;
    private ServiceTracker tracker;

    public OptionalService(Class<T> type) {
        this.type = (Class)Preconditions.checkNotNull(type, (Object)"type");
    }

    public final T getService() {
        return this.type.cast(this.tracker.getService());
    }

    public final void destroy() {
        this.tracker.close();
    }

    public final void setBundleContext(BundleContext bundleContext) {
        this.tracker = new ServiceTracker(bundleContext, this.type.getName(), null);
        this.tracker.open();
    }
}

