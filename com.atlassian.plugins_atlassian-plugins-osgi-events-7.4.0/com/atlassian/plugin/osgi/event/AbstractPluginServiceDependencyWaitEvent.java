/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 */
package com.atlassian.plugin.osgi.event;

import com.atlassian.plugin.osgi.event.PluginServiceDependencyWaitEvent;
import org.osgi.framework.Filter;

class AbstractPluginServiceDependencyWaitEvent
implements PluginServiceDependencyWaitEvent {
    protected final Filter filter;
    protected final String beanName;
    protected final String pluginKey;

    protected AbstractPluginServiceDependencyWaitEvent(String pluginKey, String beanName, Filter filter) {
        this.pluginKey = pluginKey;
        this.beanName = beanName;
        this.filter = filter;
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }

    @Override
    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public String getPluginKey() {
        return this.pluginKey;
    }
}

