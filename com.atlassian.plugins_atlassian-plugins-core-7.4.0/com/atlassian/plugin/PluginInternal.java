/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;

public interface PluginInternal
extends Plugin {
    public void setBundledPlugin(boolean var1);

    public boolean addDynamicModuleDescriptor(ModuleDescriptor<?> var1);

    public Iterable<ModuleDescriptor<?>> getDynamicModuleDescriptors();

    public boolean removeDynamicModuleDescriptor(ModuleDescriptor<?> var1);
}

