/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginInstaller;
import com.atlassian.plugin.RevertablePluginInstaller;

class NoOpRevertablePluginInstaller
implements RevertablePluginInstaller {
    private final PluginInstaller delegate;

    public NoOpRevertablePluginInstaller(PluginInstaller delegate) {
        this.delegate = delegate;
    }

    @Override
    public void revertInstalledPlugin(String pluginKey) {
    }

    @Override
    public void clearBackups() {
    }

    @Override
    public void installPlugin(String key, PluginArtifact pluginArtifact) {
        this.delegate.installPlugin(key, pluginArtifact);
    }
}

