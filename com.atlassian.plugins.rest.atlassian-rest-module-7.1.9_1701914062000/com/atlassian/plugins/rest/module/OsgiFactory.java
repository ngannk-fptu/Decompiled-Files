/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.plugins.rest.module;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public interface OsgiFactory<S> {
    public S getInstance(BundleContext var1, ServiceReference var2);
}

