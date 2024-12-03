/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 */
package com.atlassian.pocketknife.spi.lifecycle.services;

import com.atlassian.pocketknife.api.lifecycle.services.OptionalService;
import com.atlassian.pocketknife.internal.lifecycle.services.OptionalServiceImpl;
import com.google.common.base.Preconditions;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

public class OptionalServiceAccessor<T> {
    private final BundleContext bundleContext;
    private final String serviceName;

    public OptionalServiceAccessor(BundleContext bundleContext, String serviceName) {
        this.bundleContext = (BundleContext)Preconditions.checkNotNull((Object)bundleContext);
        this.serviceName = (String)Preconditions.checkNotNull((Object)serviceName);
    }

    public OptionalService<T> obtain() {
        return new OptionalServiceImpl(this.bundleContext, this.serviceName, null);
    }

    public OptionalService<T> obtain(Filter filter) {
        return new OptionalServiceImpl(this.bundleContext, this.serviceName, filter);
    }
}

