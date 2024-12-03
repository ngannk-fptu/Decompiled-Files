/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.loaders.PluginLoader;

public interface DiscardablePluginLoader
extends PluginLoader {
    public void discardPlugin(Plugin var1);
}

