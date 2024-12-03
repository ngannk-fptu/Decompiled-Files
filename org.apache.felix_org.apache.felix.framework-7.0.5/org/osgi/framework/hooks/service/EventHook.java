/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.hooks.service;

import java.util.Collection;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

@ConsumerType
public interface EventHook {
    public void event(ServiceEvent var1, Collection<BundleContext> var2);
}

