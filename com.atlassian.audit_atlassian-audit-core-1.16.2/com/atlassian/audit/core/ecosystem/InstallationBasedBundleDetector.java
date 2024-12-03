/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 */
package com.atlassian.audit.core.ecosystem;

import com.atlassian.audit.core.ecosystem.BundleDetector;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;

public class InstallationBasedBundleDetector
implements BundleDetector {
    private final PluginAccessor pluginAccessor;
    private final PluginMetadataManager pluginMetadataManager;

    public InstallationBasedBundleDetector(PluginAccessor pluginAccessor, PluginMetadataManager pluginMetadataManager) {
        this.pluginAccessor = pluginAccessor;
        this.pluginMetadataManager = pluginMetadataManager;
    }

    @Override
    public boolean isInternal(@Nonnull Bundle bundle) {
        Plugin plugin = this.pluginAccessor.getPlugin(bundle.getSymbolicName());
        return plugin == null || this.pluginMetadataManager.isSystemProvided(plugin);
    }
}

