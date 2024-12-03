/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.plugin.manager;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.plugin.ModuleDescriptor;
import java.util.Collections;
import java.util.Set;

@PublicSpi
public interface ApplicationDefinedPluginsProvider {
    public static final ApplicationDefinedPluginsProvider NO_APPLICATION_PLUGINS = descriptors -> Collections.emptySet();

    public Set<String> getPluginKeys(Iterable<ModuleDescriptor> var1);
}

