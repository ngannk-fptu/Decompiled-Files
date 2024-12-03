/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginDependencies
 *  com.atlassian.plugin.PluginState
 *  com.google.common.collect.ImmutableList$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.exception.PluginExceptionInterception;
import com.atlassian.plugin.util.PluginUtils;
import com.atlassian.plugin.util.WaitUntil;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginEnabler {
    private static final Logger log = LoggerFactory.getLogger(PluginEnabler.class);
    private static final long LAST_PLUGIN_TIMEOUT = 30000L;
    private static final long LAST_PLUGIN_WARN_TIMEOUT = 5000L;
    private final PluginAccessor pluginAccessor;
    private final PluginController pluginController;
    private final PluginExceptionInterception pluginExceptionInterception;
    private final Set<Plugin> pluginsBeingEnabled = new CopyOnWriteArraySet<Plugin>();

    public PluginEnabler(PluginAccessor pluginAccessor, PluginController pluginController, PluginExceptionInterception pluginExceptionInterception) {
        this.pluginAccessor = pluginAccessor;
        this.pluginController = pluginController;
        this.pluginExceptionInterception = pluginExceptionInterception;
    }

    Collection<Plugin> enableAllRecursively(Collection<Plugin> plugins) {
        ArrayList<Plugin> pluginsToEnable = new ArrayList<Plugin>();
        HashSet<String> requiredKeys = new HashSet<String>();
        for (Plugin plugin : plugins) {
            this.scanDependencies(plugin, requiredKeys);
        }
        for (String key : requiredKeys) {
            pluginsToEnable.add(this.pluginAccessor.getPlugin(key));
        }
        this.enable(pluginsToEnable);
        ImmutableList.Builder enabledPlugins = new ImmutableList.Builder();
        for (Plugin plugin : pluginsToEnable) {
            if (!plugin.getPluginState().equals((Object)PluginState.ENABLED)) continue;
            enabledPlugins.add((Object)plugin);
        }
        return enabledPlugins.build();
    }

    public boolean isPluginBeingEnabled(Plugin plugin) {
        return this.pluginsBeingEnabled.contains(plugin);
    }

    void enable(Collection<Plugin> plugins) {
        this.pluginsBeingEnabled.addAll(plugins);
        try {
            this.actualEnable(plugins);
        }
        finally {
            this.pluginsBeingEnabled.removeAll(plugins);
        }
    }

    private void actualEnable(Collection<Plugin> plugins) {
        log.info("Resolving {} plugins", (Object)plugins.size());
        final HashSet<Plugin> pluginsInEnablingState = new HashSet<Plugin>();
        for (Plugin plugin : plugins) {
            try {
                plugin.resolve();
            }
            catch (RuntimeException runtime) {
                log.error("Cannot resolve plugin '" + plugin.getKey() + "'", (Throwable)runtime);
            }
        }
        log.info("Enabling {} plugins: {}", (Object)plugins.size(), plugins);
        for (Plugin plugin : plugins) {
            try {
                plugin.enable();
                PluginState pluginState = plugin.getPluginState();
                if (pluginState == PluginState.ENABLING) {
                    pluginsInEnablingState.add(plugin);
                    continue;
                }
                log.info("Plugin '{}' immediately {}", (Object)plugin.getKey(), (Object)pluginState);
            }
            catch (RuntimeException ex) {
                boolean logMsg = this.pluginExceptionInterception.onEnableException(plugin, ex);
                if (!logMsg) continue;
                log.error("Unable to enable plugin " + plugin.getKey(), (Throwable)ex);
            }
        }
        if (!pluginsInEnablingState.isEmpty()) {
            log.info("Waiting for {} plugins to finish ENABLING: {}", (Object)pluginsInEnablingState.size(), pluginsInEnablingState);
            WaitUntil.invoke(new WaitUntil.WaitCondition(){
                private long singlePluginTimeout;
                private long singlePluginWarn;

                @Override
                public boolean isFinished() {
                    if (this.singlePluginTimeout > 0L && this.singlePluginTimeout < System.currentTimeMillis()) {
                        return true;
                    }
                    Iterator i = pluginsInEnablingState.iterator();
                    while (i.hasNext()) {
                        Plugin plugin = (Plugin)i.next();
                        PluginState pluginState = plugin.getPluginState();
                        if (pluginState == PluginState.ENABLING) continue;
                        log.info("Plugin '{}' is now {}", (Object)plugin.getKey(), (Object)pluginState);
                        i.remove();
                    }
                    if (PluginUtils.isAtlassianDevMode() && pluginsInEnablingState.size() == 1) {
                        long currentTime = System.currentTimeMillis();
                        if (this.singlePluginTimeout == 0L) {
                            log.info("Only one plugin left not enabled. Resetting the timeout to {} seconds.", (Object)30L);
                            this.singlePluginWarn = currentTime + 5000L;
                            this.singlePluginTimeout = currentTime + 30000L;
                        } else if (this.singlePluginWarn <= currentTime) {
                            Plugin plugin = (Plugin)pluginsInEnablingState.iterator().next();
                            long remainingWait = Math.max(0L, Math.round((double)(this.singlePluginTimeout - currentTime) / 1000.0));
                            log.warn("Plugin '{}' did not enable within {} seconds. The plugin should not take this long to enable. Will only attempt to load plugin for another '{}' seconds.", new Object[]{plugin, 5L, remainingWait});
                            this.singlePluginWarn = Long.MAX_VALUE;
                        }
                    }
                    return pluginsInEnablingState.isEmpty();
                }

                @Override
                public String getWaitMessage() {
                    return "Plugins that have yet to be enabled: (" + pluginsInEnablingState.size() + "): " + pluginsInEnablingState;
                }
            }, PluginUtils.getDefaultEnablingWaitPeriod(), TimeUnit.SECONDS, 1);
            if (!pluginsInEnablingState.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Plugin plugin : pluginsInEnablingState) {
                    sb.append(plugin.getKey()).append(',');
                    this.pluginController.disablePluginWithoutPersisting(plugin.getKey());
                }
                sb.deleteCharAt(sb.length() - 1);
                log.error("Unable to start the following plugins due to timeout while waiting for plugin to enable: {}", (Object)sb);
            }
        }
    }

    private void scanDependencies(Plugin plugin, Set<String> requiredKeys) {
        requiredKeys.add(plugin.getKey());
        PluginDependencies dependencies = plugin.getDependencies();
        HashSet dependencyKeys = new HashSet(dependencies.getMandatory());
        for (String dependencyKey : dependencyKeys) {
            if (requiredKeys.contains(dependencyKey) || this.pluginAccessor.getPlugin(dependencyKey) == null || this.pluginAccessor.isPluginEnabled(dependencyKey)) continue;
            this.scanDependencies(this.pluginAccessor.getPlugin(dependencyKey), requiredKeys);
        }
    }

    public Set<Plugin> getPluginsBeingEnabled() {
        return this.pluginsBeingEnabled;
    }
}

