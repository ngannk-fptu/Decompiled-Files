/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.manager.PluginEnabledState
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.manager;

import com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.manager.ApplicationDefinedPluginsProvider;
import com.atlassian.plugin.manager.ClusterEnvironmentProvider;
import com.atlassian.plugin.manager.PluginEnabledState;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.plugin.manager.SafeModeManager;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.parsers.SafeModeCommandLineArguments;
import com.atlassian.plugin.parsers.SafeModeCommandLineArgumentsFactory;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class DefaultSafeModeManager
implements SafeModeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSafeModeManager.class);
    private final SafeModeCommandLineArguments commandLineArguments;
    private final PluginMetadataManager pluginMetadataManager;
    private final ApplicationDefinedPluginsProvider appRelatedPluginsProvider;
    private final ClusterEnvironmentProvider clusterEnvironmentProvider;
    private final Supplier<Optional<String>> lastEnabledPluginKey = Suppliers.memoize((Supplier)new Supplier<Optional<String>>(){

        public Optional<String> get() {
            PluginPersistentState state = DefaultSafeModeManager.this.pluginPersistentStateStore.load();
            if (state.getStatesMap().isEmpty()) {
                return Optional.empty();
            }
            Optional<Map.Entry> mostRecentlyEnabled = state.getStatesMap().entrySet().stream().max(Comparator.comparingLong(entry -> ((PluginEnabledState)entry.getValue()).getTimestamp()));
            if (mostRecentlyEnabled.isPresent() && ((PluginEnabledState)mostRecentlyEnabled.get().getValue()).getTimestamp() == 0L) {
                return Optional.empty();
            }
            return mostRecentlyEnabled.map(Map.Entry::getKey);
        }
    });
    private final boolean isInSafeMode;
    private final PluginPersistentStateStore pluginPersistentStateStore;

    public DefaultSafeModeManager(PluginMetadataManager pluginMetadataManager, ClusterEnvironmentProvider clusterEnvironmentProvider, SafeModeCommandLineArgumentsFactory safeModeCommandLineArgumentsFactory, PluginPersistentStateStore pluginPersistentStateStore) {
        this(pluginMetadataManager, ApplicationDefinedPluginsProvider.NO_APPLICATION_PLUGINS, clusterEnvironmentProvider, safeModeCommandLineArgumentsFactory, pluginPersistentStateStore);
    }

    DefaultSafeModeManager(PluginMetadataManager pluginMetadataManager, ApplicationDefinedPluginsProvider appRelatedPluginsProvider, ClusterEnvironmentProvider clusterEnvironmentProvider, SafeModeCommandLineArgumentsFactory safeModeCommandLineArgumentsFactory, PluginPersistentStateStore pluginPersistentStateStore) {
        this.pluginMetadataManager = pluginMetadataManager;
        this.commandLineArguments = safeModeCommandLineArgumentsFactory.get();
        this.appRelatedPluginsProvider = appRelatedPluginsProvider;
        this.clusterEnvironmentProvider = clusterEnvironmentProvider;
        this.pluginPersistentStateStore = pluginPersistentStateStore;
        boolean bl = this.isInSafeMode = !clusterEnvironmentProvider.isInCluster() && this.commandLineArguments.isSafeMode();
        if (clusterEnvironmentProvider.isInCluster() && (this.commandLineArguments.isSafeMode() || !this.commandLineArguments.getDisabledPlugins().orElse(Collections.emptyList()).isEmpty())) {
            LOGGER.warn("Add-ons disable options from '{}' are being ignored due to start up in clustered mode!", (Object)this.commandLineArguments.getSafeModeArguments());
        }
    }

    @Override
    public boolean pluginShouldBeStarted(Plugin plugin, Iterable<ModuleDescriptor> descriptors) {
        return this.clusterEnvironmentProvider.isInCluster() || !this.isPluginDisabledByDisableLastEnabled(plugin) && !this.isPluginDisabledBySafeMode(plugin, descriptors) && !this.commandLineArguments.isDisabledByParam(plugin.getKey());
    }

    @Override
    public boolean isInSafeMode() {
        return this.isInSafeMode;
    }

    private boolean isPluginDisabledByDisableLastEnabled(Plugin plugin) {
        return this.commandLineArguments.shouldLastEnabledBeDisabled() && plugin.getKey().equals(((Optional)this.lastEnabledPluginKey.get()).orElse(null));
    }

    private boolean isPluginDisabledBySafeMode(Plugin plugin, Iterable<ModuleDescriptor> descriptors) {
        return this.commandLineArguments.isSafeMode() && !this.isSystemPlugin(plugin, descriptors);
    }

    private boolean isSystemPlugin(Plugin plugin, Iterable<ModuleDescriptor> descriptors) {
        return this.pluginMetadataManager.isSystemProvided(plugin) || !this.pluginMetadataManager.isOptional(plugin) || this.appRelatedPluginsProvider.getPluginKeys(descriptors).contains(plugin.getKey());
    }
}

