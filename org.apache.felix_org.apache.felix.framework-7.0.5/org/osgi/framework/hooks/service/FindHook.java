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
import org.osgi.framework.ServiceReference;

@ConsumerType
public interface FindHook {
    public void find(BundleContext var1, String var2, String var3, boolean var4, Collection<ServiceReference<?>> var5);
}

