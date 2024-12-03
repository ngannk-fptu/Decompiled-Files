/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.metadata;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;

interface PluginMetadata {
    public boolean applicationProvided(Plugin var1);

    public boolean required(Plugin var1);

    public boolean required(ModuleDescriptor<?> var1);
}

