/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 */
package com.atlassian.migration.agent.service.app;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import java.util.Collection;
import java.util.stream.Collectors;

public class PluginManager {
    private final PluginAccessor pluginAccessor;
    private final PluginMetadataManager pluginMetadataManager;
    private final MigrationAppAggregatorService migrationAppAggregatorService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public PluginManager(PluginAccessor pluginAccessor, PluginMetadataManager pluginMetadataManager, MigrationAppAggregatorService migrationAppAggregatorService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.pluginAccessor = pluginAccessor;
        this.pluginMetadataManager = pluginMetadataManager;
        this.migrationAppAggregatorService = migrationAppAggregatorService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    public Collection<Plugin> getActualUserInstalledPlugins() {
        return this.pluginAccessor.getPlugins().parallelStream().filter(arg_0 -> ((PluginMetadataManager)this.pluginMetadataManager).isUserInstalled(arg_0)).filter(plugin -> !this.migrationAppAggregatorService.isBlacklisted(plugin.getKey())).filter(plugin -> this.migrationDarkFeaturesManager.isTeamCalendarsMigrationDisabled() || !plugin.getKey().equals("com.atlassian.confluence.extra.team-calendars")).collect(Collectors.toList());
    }

    public Plugin getPlugin(String appKey) {
        return this.pluginAccessor.getPlugin(appKey);
    }

    public Boolean isPluginInstalled(String appKey) {
        return this.pluginAccessor.getPlugin(appKey) != null;
    }

    public Boolean isPluginEnabled(String appKey) {
        return this.pluginAccessor.getEnabledPlugin(appKey) != null;
    }
}

