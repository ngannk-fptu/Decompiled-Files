/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.hooks.service;

import java.util.Collection;
import java.util.Map;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.hooks.service.ListenerHook;

@ConsumerType
public interface EventListenerHook {
    public void event(ServiceEvent var1, Map<BundleContext, Collection<ListenerHook.ListenerInfo>> var2);
}

