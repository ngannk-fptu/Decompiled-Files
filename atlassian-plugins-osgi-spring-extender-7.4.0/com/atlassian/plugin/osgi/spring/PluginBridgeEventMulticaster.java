/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticasterAdapter
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ApplicationEventMulticaster
 *  org.springframework.context.event.SimpleApplicationEventMulticaster
 */
package com.atlassian.plugin.osgi.spring;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticasterAdapter;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

public class PluginBridgeEventMulticaster
extends OsgiBundleApplicationContextEventMulticasterAdapter {
    private volatile OsgiBundleApplicationContextListener bridgeListener;

    public PluginBridgeEventMulticaster(BundleContext bundleContext) {
        super((ApplicationEventMulticaster)new SimpleApplicationEventMulticaster());
        String filter = "(&(objectClass=" + OsgiBundleApplicationContextListener.class.getName() + ")(plugin-bridge=true))";
        try {
            ServiceReference[] refs = bundleContext.getAllServiceReferences(ApplicationListener.class.getName(), filter);
            if (refs != null && refs.length == 1) {
                this.bridgeListener = (OsgiBundleApplicationContextListener)bundleContext.getService(refs[0]);
            }
            bundleContext.addServiceListener(serviceEvent -> {
                switch (serviceEvent.getType()) {
                    case 1: 
                    case 2: {
                        this.bridgeListener = (OsgiBundleApplicationContextListener)bundleContext.getService(serviceEvent.getServiceReference());
                        break;
                    }
                    case 4: {
                        this.bridgeListener = null;
                        break;
                    }
                }
            }, filter);
        }
        catch (InvalidSyntaxException e) {
            throw new RuntimeException("Invalid LDAP filter", e);
        }
    }

    public void multicastEvent(OsgiBundleApplicationContextEvent applicationEvent) {
        super.multicastEvent(applicationEvent);
        if (this.bridgeListener != null) {
            this.bridgeListener.onOsgiApplicationEvent(applicationEvent);
        }
    }
}

