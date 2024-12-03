/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.common.Option;
import com.google.common.base.Preconditions;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class OptionalService<T>
implements InitializingBean,
DisposableBean {
    private final Class<T> type;
    private final BundleContext bundleContext;
    private ServiceTracker tracker;

    public OptionalService(Class<T> type, BundleContext bundleContext) {
        this.type = (Class)Preconditions.checkNotNull(type, (Object)"type");
        this.bundleContext = bundleContext;
    }

    protected final Option<T> getService() {
        return Option.option(this.type.cast(this.tracker.getService()));
    }

    public final void afterPropertiesSet() throws Exception {
        this.tracker = new ServiceTracker(this.bundleContext, this.type.getName(), null);
        this.tracker.open();
    }

    public final void destroy() throws Exception {
        this.tracker.close();
    }
}

