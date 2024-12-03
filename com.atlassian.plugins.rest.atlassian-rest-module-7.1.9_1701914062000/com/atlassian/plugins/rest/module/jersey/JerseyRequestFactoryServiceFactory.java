/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.plugins.rest.module.jersey;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.plugins.rest.module.jersey.JerseyRequestFactory;
import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class JerseyRequestFactoryServiceFactory
implements ServiceFactory {
    private final PluginAccessor pluginAccessor;
    private final NonMarshallingRequestFactory requestFactory;

    public JerseyRequestFactoryServiceFactory(PluginAccessor pluginAccessor, NonMarshallingRequestFactory requestFactory) {
        this.pluginAccessor = pluginAccessor;
        this.requestFactory = requestFactory;
    }

    public Object getService(Bundle bundle2, ServiceRegistration serviceRegistration) {
        Plugin plugin = this.pluginAccessor.getPlugin(OsgiHeaderUtil.getPluginKey((Bundle)bundle2));
        if (!(plugin instanceof ContainerManagedPlugin)) {
            throw new IllegalStateException("Can't create RequestFactory for plugin " + plugin + " " + plugin.getClass().getCanonicalName() + " as it is not a ContainerManagedPlugin");
        }
        return new JerseyRequestFactory((NonMarshallingRequestFactory<? extends Request>)this.requestFactory, plugin, bundle2);
    }

    public void ungetService(Bundle bundle2, ServiceRegistration serviceRegistration, Object o) {
        ((JerseyRequestFactory)o).destroy();
    }
}

