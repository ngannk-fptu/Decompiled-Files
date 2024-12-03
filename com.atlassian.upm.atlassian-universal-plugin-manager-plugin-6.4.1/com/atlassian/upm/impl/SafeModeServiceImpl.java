/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.impl;

import com.atlassian.plugin.PluginState;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.PluginsEnablementStateStore;
import com.atlassian.upm.SafeModeService;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginEnablementService;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginModuleNotFoundException;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.PluginsEnablementState;
import com.atlassian.upm.core.impl.SafeModeAccessorImpl;
import com.atlassian.upm.core.log.AuditLogService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SafeModeServiceImpl
extends SafeModeAccessorImpl
implements SafeModeService {
    private static final Logger log = LoggerFactory.getLogger(SafeModeServiceImpl.class);
    private final PluginEnablementService enabler;
    private final AuditLogService auditLogger;
    private final PluginsEnablementStateStore enablementStateStore;
    private final TransactionTemplate txTemplate;
    private final PluginRetriever pluginRetriever;
    private final ApplicationPluginsManager applicationPluginsManager;

    public SafeModeServiceImpl(UpmPluginAccessor pluginAccessor, PluginEnablementService enabler, AuditLogService auditLogger, PluginsEnablementStateStore enablementStateStore, TransactionTemplate txTemplate, PluginMetadataAccessor metadata, PluginRetriever pluginRetriever, ApplicationPluginsManager applicationPluginsManager) {
        super(pluginAccessor, pluginRetriever, metadata, enablementStateStore);
        this.enabler = Objects.requireNonNull(enabler, "enabler");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.enablementStateStore = Objects.requireNonNull(enablementStateStore, "enablementStateStore");
        this.txTemplate = Objects.requireNonNull(txTemplate, "txTemplate");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "applicationPluginsManager");
    }

    @Override
    public boolean enterSafeMode() {
        this.auditLogger.logI18nMessage("upm.auditLog.safeMode.enter.start", new String[0]);
        List<Plugin> plugins = Iterables.toList(this.pluginRetriever.getPlugins());
        PluginsEnablementState config = this.getCurrentConfiguration(plugins);
        this.txTemplate.execute(() -> {
            try {
                this.enablementStateStore.saveConfiguration(config);
            }
            catch (RuntimeException e) {
                this.auditLogger.logI18nMessage("upm.auditLog.safeMode.enter.failure", new String[0]);
                throw e;
            }
            return null;
        });
        return (Boolean)this.txTemplate.execute(() -> {
            try {
                boolean successful;
                boolean bl = successful = this.isSafeMode() && this.disableAllUserInstalledPlugins(plugins, config);
                if (successful) {
                    this.auditLogger.logI18nMessage("upm.auditLog.safeMode.enter.success", new String[0]);
                } else {
                    this.enablementStateStore.removeSavedConfiguration();
                    this.auditLogger.logI18nMessage("upm.auditLog.safeMode.enter.failure", new String[0]);
                }
                return successful;
            }
            catch (RuntimeException e) {
                this.auditLogger.logI18nMessage("upm.auditLog.safeMode.enter.failure", new String[0]);
                throw e;
            }
        });
    }

    @Override
    public void exitSafeMode(boolean keepState) {
        this.auditLogger.logI18nMessage("upm.auditLog.safeMode.exit.start", new String[0]);
        this.txTemplate.execute(() -> {
            try {
                if (!keepState) {
                    Option<PluginsEnablementState> config = this.enablementStateStore.getSavedConfiguration();
                    if (config.isDefined()) {
                        for (PluginsEnablementState savedConfiguration : config) {
                            this.applyConfiguration(savedConfiguration);
                        }
                    } else {
                        throw new SafeModeService.MissingSavedConfigurationException();
                    }
                }
                this.enablementStateStore.removeSavedConfiguration();
                this.auditLogger.logI18nMessage("upm.auditLog.safeMode.exit.success", new String[0]);
            }
            catch (SafeModeService.MissingSavedConfigurationException msce) {
                this.auditLogger.logI18nMessage("upm.auditLog.safeMode.exit.failure.missing.configuration", new String[0]);
                throw msce;
            }
            catch (SafeModeService.PluginStateUpdateException psue) {
                this.auditLogger.logI18nMessage("upm.auditLog.safeMode.exit.failure.restoring.plugin.state", psue.getPlugin().getName(), psue.getPlugin().getKey());
                throw psue;
            }
            catch (SafeModeService.PluginModuleStateUpdateException pmsue) {
                this.auditLogger.logI18nMessage("upm.auditLog.safeMode.exit.failure.restoring.plugin.module.state", pmsue.getModule().getName(), pmsue.getModule().getPlugin().getName(), pmsue.getModule().getCompleteKey());
                throw pmsue;
            }
            catch (RuntimeException re) {
                this.auditLogger.logI18nMessage("upm.auditLog.safeMode.exit.failure", new String[0]);
                throw re;
            }
            return null;
        });
    }

    private boolean disableAllUserInstalledPlugins(List<Plugin> allPlugins, PluginsEnablementState previousPluginsState) {
        List<Plugin> pluginsToDisable;
        List<Plugin> disableOrder;
        Set<String> applicationPluginKeys = this.applicationPluginsManager.getApplicationRelatedPluginKeys();
        if (!applicationPluginKeys.isEmpty()) {
            log.info("Excluding the following plugins from Safe Mode because they are parts of applications: " + applicationPluginKeys);
        }
        if (!this.disablePlugins(disableOrder = this.sortByDependencies(false, pluginsToDisable = allPlugins.stream().filter(plugin -> !plugin.isUpmPlugin() && this.metadata.isUserInstalled((Plugin)plugin) && PluginState.ENABLED.equals((Object)plugin.getPluginState()) && !applicationPluginKeys.contains(plugin.getKey())).collect(Collectors.toList())))) {
            this.applyConfigurations(Iterables.toList(previousPluginsState.getPlugins()), allPlugins, false);
            return false;
        }
        return true;
    }

    private boolean disablePlugins(Iterable<Plugin> plugins) {
        for (Plugin p : plugins) {
            if (!p.isEnabled() || this.enabler.disablePlugin(p.getKey())) continue;
            return false;
        }
        return true;
    }

    @Override
    public void applyConfiguration(PluginsEnablementState configuration) {
        List<Plugin> allPlugins = Iterables.toList(this.pluginRetriever.getPlugins());
        List<PluginsEnablementState.PluginState> configurationPlugins = Iterables.toList(configuration.getPlugins());
        List<PluginsEnablementState.PluginState> previousPluginsState = this.getCurrentPluginsConfigurationState(allPlugins);
        try {
            this.applyConfigurations(configurationPlugins, allPlugins, true);
        }
        catch (RuntimeException e) {
            this.applyConfigurations(previousPluginsState, allPlugins, false);
            throw e;
        }
    }

    private void applyConfigurations(List<PluginsEnablementState.PluginState> configs, List<Plugin> allPlugins, boolean terminateOnFailure) {
        this.applyConfigurationsInternal(configs, allPlugins, true, terminateOnFailure);
        this.applyConfigurationsInternal(configs, allPlugins, false, terminateOnFailure);
    }

    private void applyConfigurationsInternal(List<PluginsEnablementState.PluginState> allConfigs, List<Plugin> allPlugins, boolean enable, boolean terminateOnFailure) {
        List configs = allConfigs.stream().filter(PluginsEnablementState.pluginStateEnabled(enable)).collect(Collectors.toList());
        Map<String, PluginsEnablementState.PluginState> configsByKey = configs.stream().collect(Collectors.toMap(PluginsEnablementState.PluginState::getKey, pluginState -> pluginState));
        List plugins = allPlugins.stream().filter(p -> configsByKey.containsKey(p.getKey())).collect(Collectors.toList());
        List<Plugin> pluginsRequiringChange = plugins.stream().filter(Plugins.enabled(!enable, this.pluginRetriever)).collect(Collectors.toList());
        List<Plugin> sortedPlugins = this.sortByDependencies(enable, pluginsRequiringChange);
        for (Plugin p2 : sortedPlugins) {
            PluginsEnablementState.PluginState pluginConfiguration = Option.option(configsByKey.get(p2.getKey())).getOrElse(new PluginsEnablementState.PluginState.Builder(p2.getKey(), true, Collections.emptyList()).build());
            if (!this.setPluginState(pluginConfiguration)) {
                if (!terminateOnFailure) continue;
                throw new SafeModeService.PluginStateUpdateException(p2, pluginConfiguration.isEnabled());
            }
            for (Plugin.Module m : p2.getModules()) {
                boolean shouldBeEnabled;
                if (this.setPluginModuleState(m, shouldBeEnabled = pluginConfiguration.isModuleEnabled(m.getCompleteKey())) || !terminateOnFailure) continue;
                throw new SafeModeService.PluginModuleStateUpdateException(m, shouldBeEnabled);
            }
        }
    }

    private List<PluginsEnablementState.PluginState> getCurrentPluginsConfigurationState(List<Plugin> plugins) {
        return this.transformPluginToPluginConfigurations(plugins);
    }

    private boolean setPluginState(PluginsEnablementState.PluginState pluginConfiguration) {
        String pluginKey = pluginConfiguration.getKey();
        if (pluginConfiguration.isEnabled() != this.pluginRetriever.isPluginEnabled(pluginKey)) {
            if (pluginConfiguration.isEnabled()) {
                return this.enabler.enablePlugin(pluginKey);
            }
            return this.enabler.disablePlugin(pluginKey);
        }
        return true;
    }

    private boolean setPluginModuleState(Plugin.Module pluginModule, boolean shouldBeEnabled) {
        if (shouldBeEnabled != this.pluginRetriever.isPluginModuleEnabled(pluginModule.getCompleteKey())) {
            if (shouldBeEnabled) {
                try {
                    return this.enabler.enablePluginModule(pluginModule.getCompleteKey());
                }
                catch (PluginModuleNotFoundException e) {
                    return true;
                }
            }
            return this.enabler.disablePluginModule(pluginModule.getCompleteKey());
        }
        return true;
    }

    private List<Plugin> sortByDependencies(boolean dependenciesFirst, List<Plugin> plugins) {
        Set<String> allPluginKeys = Collections.unmodifiableSet(plugins.stream().map(Plugin::getKey).collect(Collectors.toSet()));
        List<Plugin> sortedWithDependenciesFirst = this.getWithDependenciesFirst(plugins, allPluginKeys, new HashSet<String>());
        if (dependenciesFirst) {
            return sortedWithDependenciesFirst;
        }
        ArrayList<Plugin> list = new ArrayList<Plugin>(sortedWithDependenciesFirst);
        Collections.reverse(list);
        return Collections.unmodifiableList(list);
    }

    private List<Plugin> getWithDependenciesFirst(Iterable<Plugin> plugins, Set<String> allPluginKeys, Set<String> pluginKeysAlreadyVisited) {
        ArrayList<Plugin> list = new ArrayList<Plugin>();
        for (Plugin p : plugins) {
            String key = p.getKey();
            if (!allPluginKeys.contains(key) || pluginKeysAlreadyVisited.contains(key)) continue;
            pluginKeysAlreadyVisited.add(key);
            List<Plugin> deps = p.getPlugin().getDependencies().getAll().stream().map(Plugins.toInstalledPlugin(this.pluginRetriever)).filter(Option::isDefined).map(Option::get).collect(Collectors.toList());
            if (!deps.isEmpty()) {
                list.addAll(this.getWithDependenciesFirst(deps, allPluginKeys, pluginKeysAlreadyVisited));
            }
            list.add(p);
        }
        return Collections.unmodifiableList(list);
    }
}

