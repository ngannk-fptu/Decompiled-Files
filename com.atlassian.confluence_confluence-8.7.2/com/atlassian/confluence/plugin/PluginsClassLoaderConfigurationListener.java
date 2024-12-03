/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.PluginsClassLoaderAvailableEvent;
import com.atlassian.confluence.util.ConfluenceUberClassLoader;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public final class PluginsClassLoaderConfigurationListener
implements ApplicationListener {
    private ConfluenceUberClassLoader delegationClassLoader;

    public void onApplicationEvent(ApplicationEvent event) {
        if (!(event instanceof PluginsClassLoaderAvailableEvent) || this.delegationClassLoader == null) {
            return;
        }
        PluginsClassLoaderAvailableEvent classLoaderAvailableEvent = (PluginsClassLoaderAvailableEvent)event;
        this.delegationClassLoader.setPluginsClassLoader(classLoaderAvailableEvent.getPluginsClassLoader());
    }

    public void setDelegationClassLoader(ConfluenceUberClassLoader delegationClassLoader) {
        this.delegationClassLoader = delegationClassLoader;
    }
}

