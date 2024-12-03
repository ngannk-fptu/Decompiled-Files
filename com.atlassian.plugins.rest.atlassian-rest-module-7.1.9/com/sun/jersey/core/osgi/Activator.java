/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 */
package com.sun.jersey.core.osgi;

import com.sun.jersey.core.osgi.OsgiRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator
implements BundleActivator {
    public void start(BundleContext context) throws Exception {
        OsgiRegistry.getInstance();
    }

    public void stop(BundleContext context) throws Exception {
    }
}

