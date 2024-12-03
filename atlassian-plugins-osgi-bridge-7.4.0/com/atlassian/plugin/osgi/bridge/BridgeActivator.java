/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.plugin.osgi.bridge;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.osgi.bridge.PluginRetrievalServiceFactory;
import com.atlassian.plugin.osgi.bridge.SpringContextEventBridge;
import com.atlassian.plugin.osgi.bridge.SpringOsgiEventBridge;
import com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService;
import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class BridgeActivator
implements BundleActivator {
    public void start(BundleContext bundleContext) {
        PluginEventManager pluginEventManager = this.getHostComponent(bundleContext, PluginEventManager.class);
        bundleContext.registerService(OsgiBundleApplicationContextListener.class.getName(), (Object)new SpringOsgiEventBridge(pluginEventManager), null);
        Hashtable<String, String> dict = new Hashtable<String, String>();
        ((Dictionary)dict).put("plugin-bridge", "true");
        bundleContext.registerService(OsgiBundleApplicationContextListener.class.getName(), (Object)new SpringContextEventBridge(pluginEventManager), dict);
        PluginAccessor pluginAccessor = this.getHostComponent(bundleContext, PluginAccessor.class);
        bundleContext.registerService(PluginRetrievalService.class.getName(), (Object)new PluginRetrievalServiceFactory(pluginAccessor), null);
    }

    private <T> T getHostComponent(BundleContext bundleContext, Class<T> componentClass) {
        ServiceReference ref = bundleContext.getServiceReference(componentClass.getName());
        if (ref == null) {
            throw new IllegalStateException("The " + componentClass.getName() + " service must be exported from the application");
        }
        return (T)bundleContext.getService(ref);
    }

    public void stop(BundleContext bundleContext) {
    }
}

