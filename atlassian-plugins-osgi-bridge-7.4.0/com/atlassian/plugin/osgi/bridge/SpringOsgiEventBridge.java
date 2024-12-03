/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginContainerFailedEvent
 *  com.atlassian.plugin.event.events.PluginContainerRefreshedEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent
 */
package com.atlassian.plugin.osgi.bridge;

import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginContainerFailedEvent;
import com.atlassian.plugin.event.events.PluginContainerRefreshedEvent;
import com.atlassian.plugin.osgi.bridge.PluginBundleUtils;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;

public class SpringOsgiEventBridge
implements OsgiBundleApplicationContextListener {
    private final PluginEventManager pluginEventManager;

    public SpringOsgiEventBridge(PluginEventManager pluginEventManager) {
        this.pluginEventManager = pluginEventManager;
    }

    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent evt) {
        if (evt instanceof OsgiBundleContextFailedEvent) {
            OsgiBundleContextFailedEvent e = (OsgiBundleContextFailedEvent)evt;
            this.pluginEventManager.broadcast((Object)new PluginContainerFailedEvent((Object)e.getApplicationContext(), PluginBundleUtils.getPluginKey(e.getBundle()), e.getFailureCause()));
        } else if (evt instanceof OsgiBundleContextRefreshedEvent) {
            OsgiBundleContextRefreshedEvent e = (OsgiBundleContextRefreshedEvent)evt;
            this.pluginEventManager.broadcast((Object)new PluginContainerRefreshedEvent((Object)e.getApplicationContext(), PluginBundleUtils.getPluginKey(e.getBundle())));
        }
    }
}

