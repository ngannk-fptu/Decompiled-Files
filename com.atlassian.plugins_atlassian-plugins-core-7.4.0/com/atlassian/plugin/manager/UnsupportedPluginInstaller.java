/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginInstaller;

class UnsupportedPluginInstaller
implements PluginInstaller {
    UnsupportedPluginInstaller() {
    }

    @Override
    public void installPlugin(String key, PluginArtifact pluginArtifact) {
        throw new UnsupportedOperationException("Dynamic plugin installation is not supported");
    }
}

