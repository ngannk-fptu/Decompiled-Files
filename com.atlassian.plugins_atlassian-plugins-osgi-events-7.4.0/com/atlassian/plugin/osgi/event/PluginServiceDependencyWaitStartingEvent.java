/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 */
package com.atlassian.plugin.osgi.event;

import com.atlassian.plugin.osgi.event.AbstractPluginServiceDependencyWaitEvent;
import org.osgi.framework.Filter;

public class PluginServiceDependencyWaitStartingEvent
extends AbstractPluginServiceDependencyWaitEvent {
    private final long waitTime;

    public PluginServiceDependencyWaitStartingEvent(String pluginKey, String beanName, Filter filter, long waitTime) {
        super(pluginKey, beanName, filter);
        this.waitTime = waitTime;
    }

    public long getWaitTime() {
        return this.waitTime;
    }
}

