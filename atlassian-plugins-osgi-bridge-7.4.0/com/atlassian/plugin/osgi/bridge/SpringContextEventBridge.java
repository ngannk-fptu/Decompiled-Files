/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitEndedEvent
 *  com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitStartingEvent
 *  com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitTimedOutEvent
 *  org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.eclipse.gemini.blueprint.extender.event.BootstrappingDependencyEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitEndedEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitStartingEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitTimedOutEvent
 *  org.eclipse.gemini.blueprint.service.importer.support.AbstractOsgiServiceImportFactoryBean
 *  org.osgi.framework.Bundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.bridge;

import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.osgi.bridge.PluginBundleUtils;
import com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitEndedEvent;
import com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitStartingEvent;
import com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitTimedOutEvent;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.extender.event.BootstrappingDependencyEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitEndedEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitStartingEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitTimedOutEvent;
import org.eclipse.gemini.blueprint.service.importer.support.AbstractOsgiServiceImportFactoryBean;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringContextEventBridge
implements OsgiBundleApplicationContextListener {
    private static final Logger log = LoggerFactory.getLogger(SpringContextEventBridge.class);
    private final PluginEventManager pluginEventManager;

    public SpringContextEventBridge(PluginEventManager pluginEventManager) {
        this.pluginEventManager = pluginEventManager;
    }

    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent osgiEvent) {
        if (osgiEvent instanceof BootstrappingDependencyEvent) {
            OsgiServiceDependencyEvent event = ((BootstrappingDependencyEvent)osgiEvent).getDependencyEvent();
            if (log.isDebugEnabled()) {
                log.debug("Handling osgi application context event: {}", (Object)event);
            }
            String beanName = event.getServiceDependency().getBeanName();
            String pluginKey = null;
            if (event.getSource() != null) {
                if (event.getSource() instanceof ConfigurableOsgiBundleApplicationContext) {
                    Bundle bundle = ((ConfigurableOsgiBundleApplicationContext)event.getSource()).getBundle();
                    pluginKey = PluginBundleUtils.getPluginKey(bundle);
                } else if (event.getSource() instanceof AbstractOsgiServiceImportFactoryBean) {
                    AbstractOsgiServiceImportFactoryBean bean = (AbstractOsgiServiceImportFactoryBean)event.getSource();
                    if (beanName == null) {
                        beanName = bean.getBeanName();
                    }
                    if (bean.getBundleContext() != null) {
                        pluginKey = PluginBundleUtils.getPluginKey(bean.getBundleContext().getBundle());
                    }
                }
            }
            if (pluginKey == null && log.isDebugEnabled()) {
                log.debug("Cannot determine the plugin key for event: {} and source: {}", (Object)event, event.getSource());
            }
            if (event instanceof OsgiServiceDependencyWaitStartingEvent) {
                this.pluginEventManager.broadcast((Object)new PluginServiceDependencyWaitStartingEvent(pluginKey, beanName, event.getServiceDependency().getServiceFilter(), ((OsgiServiceDependencyWaitStartingEvent)event).getTimeToWait()));
            } else if (event instanceof OsgiServiceDependencyWaitEndedEvent) {
                this.pluginEventManager.broadcast((Object)new PluginServiceDependencyWaitEndedEvent(pluginKey, beanName, event.getServiceDependency().getServiceFilter(), ((OsgiServiceDependencyWaitEndedEvent)event).getElapsedTime()));
            } else if (event instanceof OsgiServiceDependencyWaitTimedOutEvent) {
                this.pluginEventManager.broadcast((Object)new PluginServiceDependencyWaitTimedOutEvent(pluginKey, beanName, event.getServiceDependency().getServiceFilter(), ((OsgiServiceDependencyWaitTimedOutEvent)event).getElapsedTime()));
            }
        }
    }
}

