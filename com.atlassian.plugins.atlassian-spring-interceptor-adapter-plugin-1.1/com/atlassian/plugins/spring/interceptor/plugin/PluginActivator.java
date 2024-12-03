/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.plugins.spring.interceptor.plugin;

import com.atlassian.plugins.spring.interceptor.plugin.InterceptorRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PluginActivator
implements BundleActivator {
    private InterceptorRegistry registry;

    public void start(BundleContext bundleContext) throws Exception {
        this.registry = new InterceptorRegistry(bundleContext);
        this.registry.start();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        this.registry.stop();
        this.registry = null;
    }
}

