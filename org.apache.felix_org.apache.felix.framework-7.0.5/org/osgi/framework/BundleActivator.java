/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.BundleContext;

@ConsumerType
public interface BundleActivator {
    public void start(BundleContext var1) throws Exception;

    public void stop(BundleContext var1) throws Exception;
}

