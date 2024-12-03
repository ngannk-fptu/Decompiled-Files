/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.confluence.plugins.pulp.wrm;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class IsUserInstalledPlugin
implements Predicate<Plugin> {
    private final PluginMetadataManager pluginMetadataManager;

    @Inject
    public IsUserInstalledPlugin(@ComponentImport PluginMetadataManager pluginMetadataManager) {
        this.pluginMetadataManager = Objects.requireNonNull(pluginMetadataManager);
    }

    @Override
    public final boolean test(Plugin plugin) {
        return plugin != null && this.pluginMetadataManager.isUserInstalled(plugin);
    }
}

