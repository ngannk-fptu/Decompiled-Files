/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 */
package com.atlassian.plugin.osgi.event;

import org.osgi.framework.Filter;

public interface PluginServiceDependencyWaitEvent {
    public Filter getFilter();

    public String getBeanName();

    public String getPluginKey();
}

