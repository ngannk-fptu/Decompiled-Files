/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 */
package com.atlassian.plugin.osgi.event;

import com.atlassian.plugin.osgi.event.AbstractPluginServiceDependencyWaitEvent;
import org.osgi.framework.Filter;

public class PluginServiceDependencyWaitEndedEvent
extends AbstractPluginServiceDependencyWaitEvent {
    private final long elapsedTime;

    public PluginServiceDependencyWaitEndedEvent(String pluginKey, String beanName, Filter filter, long elapsedTime) {
        super(pluginKey, beanName, filter);
        this.elapsedTime = elapsedTime;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }
}

