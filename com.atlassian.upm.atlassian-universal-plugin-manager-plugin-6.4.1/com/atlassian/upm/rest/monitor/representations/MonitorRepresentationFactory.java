/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.rest.monitor.representations;

import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.rest.monitor.representations.PluginStateCollectionRep;
import com.atlassian.upm.rest.monitor.representations.PluginStateRep;

public interface MonitorRepresentationFactory {
    public PluginStateRep createPluginStateRep(Plugin var1);

    public PluginStateCollectionRep createPluginStateCollectionRep(Iterable<Plugin> var1);
}

