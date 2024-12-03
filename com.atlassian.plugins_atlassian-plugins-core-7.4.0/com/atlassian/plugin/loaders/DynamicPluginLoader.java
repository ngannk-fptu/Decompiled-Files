/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.loaders.PluginLoader;

public interface DynamicPluginLoader
extends PluginLoader {
    public String canLoad(PluginArtifact var1);
}

