/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.container.felix.FelixOsgiContainerManager
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.impl.osgi;

import com.atlassian.confluence.impl.osgi.OsgiProxyFactory;
import com.atlassian.confluence.impl.osgi.OsgiServiceRegistry;
import com.atlassian.plugin.osgi.container.felix.FelixOsgiContainerManager;
import com.google.common.collect.Maps;
import java.util.Map;

public class OsgiServiceRegistryImpl
implements OsgiServiceRegistry {
    private final FelixOsgiContainerManager osgiContainerManager;
    private Map<Class<?>, Object> serviceProxies;

    public OsgiServiceRegistryImpl(FelixOsgiContainerManager osgiContainerManager) {
        this.osgiContainerManager = osgiContainerManager;
        this.serviceProxies = Maps.newHashMap();
    }

    @Override
    public synchronized <T> T getService(Class<T> clazz) {
        Object proxy = this.serviceProxies.get(clazz);
        if (proxy != null) {
            return (T)proxy;
        }
        proxy = OsgiProxyFactory.createProxy(clazz, () -> this.osgiContainerManager.getServiceTracker(clazz.getName()));
        this.serviceProxies.put(clazz, proxy);
        return (T)proxy;
    }
}

