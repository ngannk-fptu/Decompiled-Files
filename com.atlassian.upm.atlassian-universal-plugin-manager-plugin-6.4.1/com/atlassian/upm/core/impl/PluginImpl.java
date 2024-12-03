/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.impl.UnloadablePlugin
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.impl;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginFactory;
import com.atlassian.upm.core.PluginMetadataAccessor;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginImpl
implements Plugin {
    private final com.atlassian.plugin.Plugin plugin;
    private final PluginAttributes attributes;
    private final Map<String, Plugin.Module> modules;
    private final boolean userInstalled;
    private static final Logger log = LoggerFactory.getLogger(PluginImpl.class);
    private static Predicate<Plugin.Module> hasUnrecognisableType = module -> !module.hasRecognisableType();

    PluginImpl(com.atlassian.plugin.Plugin plugin, PluginAttributes attributes, PluginFactory pluginFactory, PluginMetadataAccessor metadata) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.attributes = Objects.requireNonNull(attributes, "attributes");
        HashMap<String, Plugin.Module> modulesBuilder = new HashMap<String, Plugin.Module>();
        HashSet<String> moduleKeyset = new HashSet<String>();
        for (ModuleDescriptor moduleDescriptor : plugin.getModuleDescriptors()) {
            if (moduleDescriptor.getKey() == null) {
                log.warn("Found plugin (with key '" + plugin.getKey() + "') containing module with null key. Ignoring module.");
                continue;
            }
            if (moduleKeyset.contains(moduleDescriptor.getKey())) {
                log.warn("Duplicate module key detected for plugin " + plugin.getKey() + ": " + moduleDescriptor.getKey());
                continue;
            }
            modulesBuilder.put(moduleDescriptor.getKey(), pluginFactory.createModule(moduleDescriptor, this));
            moduleKeyset.add(moduleDescriptor.getKey());
        }
        this.modules = Collections.unmodifiableMap(modulesBuilder);
        this.userInstalled = metadata.isUserInstalled(this);
    }

    @Override
    public com.atlassian.plugin.Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getName() {
        return this.attributes.getName();
    }

    @Override
    public String getKey() {
        return this.plugin.getKey();
    }

    @Override
    public Iterable<Plugin.Module> getModules() {
        return this.modules.values();
    }

    @Override
    public Option<Plugin.Module> getModule(String key) {
        return Option.option(this.modules.get(key));
    }

    @Override
    public boolean isConnect() {
        return this.attributes.isConnect();
    }

    @Override
    public boolean isEnabledByDefault() {
        return this.plugin.isEnabledByDefault() || this.isConnect();
    }

    @Override
    public boolean isEnabled() {
        return this.attributes.isEnabled();
    }

    @Override
    public PluginInformation getPluginInformation() {
        return this.plugin.getPluginInformation();
    }

    @Override
    public PluginState getPluginState() {
        return this.plugin.getPluginState();
    }

    @Override
    public boolean isStaticPlugin() {
        return !this.plugin.isDynamicallyLoaded();
    }

    @Override
    public boolean isUserInstalled() {
        return this.userInstalled;
    }

    @Override
    public boolean isUpmPlugin() {
        return this.attributes.isUpmPlugin();
    }

    @Override
    public boolean isUninstallable() {
        return !this.isStaticPlugin() && this.isUserInstalled() && !this.isUpmPlugin() && this.plugin.isUninstallable() && !this.attributes.isUninstallPreventedByAdditionalCriteria();
    }

    @Override
    public boolean isUnloadable() {
        return this.plugin instanceof UnloadablePlugin;
    }

    @Override
    public boolean isBundledPlugin() {
        return this.plugin.isBundledPlugin();
    }

    @Override
    public Option<Boolean> isUpdateAvailable() {
        return this.attributes.isUpdateAvailable();
    }

    public String toString() {
        return this.plugin.getKey();
    }

    @Override
    public String getVersion() {
        return this.plugin.getPluginInformation().getVersion();
    }

    @Override
    public boolean hasUnrecognisedModuleTypes() {
        return this.modules.values().stream().anyMatch(hasUnrecognisableType);
    }

    @Override
    public PluginRestartState getRestartState() {
        return this.attributes.getRestartState();
    }

    public static class PluginAttributes {
        private final String name;
        private final boolean uninstallPreventedByAdditionalCriteria;
        private final Option<Boolean> updateAvailable;
        private final PluginRestartState restartState;
        private final boolean connect;
        private final boolean enabled;
        private final boolean upmPlugin;

        public PluginAttributes(boolean connect, boolean enabled, String name, PluginRestartState restartState, boolean uninstallPreventedByAdditionalCriteria, Option<Boolean> updateAvailable, boolean upmPlugin) {
            this.connect = connect;
            this.enabled = enabled;
            this.name = name;
            this.restartState = restartState;
            this.uninstallPreventedByAdditionalCriteria = uninstallPreventedByAdditionalCriteria;
            this.updateAvailable = updateAvailable;
            this.upmPlugin = upmPlugin;
        }

        public String getName() {
            return this.name;
        }

        public boolean isUninstallPreventedByAdditionalCriteria() {
            return this.uninstallPreventedByAdditionalCriteria;
        }

        public boolean isConnect() {
            return this.connect;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public PluginRestartState getRestartState() {
            return this.restartState;
        }

        public Option<Boolean> isUpdateAvailable() {
            return this.updateAvailable;
        }

        public boolean isUpmPlugin() {
            return this.upmPlugin;
        }
    }
}

