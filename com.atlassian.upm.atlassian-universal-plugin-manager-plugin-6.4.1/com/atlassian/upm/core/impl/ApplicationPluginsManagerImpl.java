/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.application.api.Application
 *  com.atlassian.application.api.ApplicationManager
 *  com.atlassian.application.api.ApplicationPlugin
 *  com.atlassian.application.api.PluginApplication
 *  com.atlassian.application.host.plugin.ApplicationMetaDataModuleDescriptor
 *  com.atlassian.application.host.plugin.PluginApplicationMetaData
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package com.atlassian.upm.core.impl;

import com.atlassian.application.api.Application;
import com.atlassian.application.api.ApplicationManager;
import com.atlassian.application.api.ApplicationPlugin;
import com.atlassian.application.api.PluginApplication;
import com.atlassian.application.host.plugin.ApplicationMetaDataModuleDescriptor;
import com.atlassian.application.host.plugin.PluginApplicationMetaData;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.HostingType;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.VersionAwareHostApplicationInformation;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApplicationPluginsManagerImpl
implements ApplicationPluginsManager {
    private final VersionAwareHostApplicationInformation versionAwareHostApplicationInformation;
    private final UpmAppManager upmAppManager;
    private final PluginMetadataManager metadata;
    private final boolean applicationDescriptorExists;
    private final PluginRetriever pluginRetriever;
    private final Function<ApplicationManager, Set<String>> getApplicationRelatedPluginKeysFromAppManager = new Function<ApplicationManager, Set<String>>(){

        public Set<String> apply(ApplicationManager manager) {
            HashSet keys = Sets.newHashSet();
            for (Application a : manager.getApplications()) {
                if (!(a instanceof PluginApplication)) continue;
                for (ApplicationPlugin ap : ((PluginApplication)a).getPlugins()) {
                    if (!ApplicationPluginsManagerImpl.this.isNotDataCenterBundleOnServer(ap.getPluginKey())) continue;
                    keys.add(ap.getPluginKey());
                }
            }
            return ImmutableSet.copyOf((Collection)keys);
        }
    };

    public ApplicationPluginsManagerImpl(VersionAwareHostApplicationInformation versionAwareHostApplicationInformation, UpmAppManager upmAppManager, PluginMetadataManager metadata, PluginRetriever pluginRetriever) {
        boolean descriptorExists;
        this.versionAwareHostApplicationInformation = versionAwareHostApplicationInformation;
        this.upmAppManager = upmAppManager;
        this.metadata = metadata;
        this.pluginRetriever = pluginRetriever;
        try {
            Class<ApplicationMetaDataModuleDescriptor> clazz = ApplicationMetaDataModuleDescriptor.class;
            descriptorExists = true;
        }
        catch (NoClassDefFoundError e) {
            descriptorExists = false;
        }
        this.applicationDescriptorExists = descriptorExists;
    }

    @Override
    public Map<String, UpmAppManager.ApplicationDescriptorModuleInfo> getApplicationRelatedPlugins(Iterable<com.atlassian.plugin.Plugin> plugins) {
        if (!this.upmAppManager.isApplicationSupportEnabled()) {
            return ImmutableMap.of();
        }
        HashMap map = Maps.newHashMap();
        for (com.atlassian.plugin.Plugin plugin : plugins) {
            if (map.containsKey(plugin.getKey()) || !this.metadata.isUserInstalled(plugin)) continue;
            for (Pair<String, UpmAppManager.ApplicationDescriptorModuleInfo> ap : this.getPluginsInApplication(plugin)) {
                if (!this.isNotDataCenterBundleOnServer(ap.first())) continue;
                map.put(ap.first(), ap.second());
            }
        }
        return ImmutableMap.copyOf((Map)map);
    }

    @Override
    public Set<String> getApplicationRelatedPluginKeys() {
        if (!this.upmAppManager.isApplicationSupportEnabled()) {
            return ImmutableSet.of();
        }
        return this.upmAppManager.getAppManager().map(this.getApplicationRelatedPluginKeysFromAppManager).getOrElse(ImmutableSet.of());
    }

    @Override
    public boolean isApplication(com.atlassian.plugin.Plugin plugin) {
        return this.getApplicationKey(plugin).isDefined();
    }

    @Override
    public Option<String> getApplicationKey(com.atlassian.plugin.Plugin plugin) {
        if (!this.upmAppManager.isApplicationSupportEnabled()) {
            return Option.none();
        }
        return this.safeGetAndTransform(plugin, new Function<ModuleDescriptor<?>, String>(){

            public String apply(ModuleDescriptor<?> md) {
                return ((ApplicationMetaDataModuleDescriptor)md).getApplicationKey().value();
            }
        });
    }

    @Override
    public boolean isUninstallable(String pluginKey) {
        Option<Plugin> pluginOption = this.pluginRetriever.getPlugin(pluginKey);
        return !HostingType.SERVER.equals((Object)this.versionAwareHostApplicationInformation.getHostingType()) || !this.versionAwareHostApplicationInformation.isJiraPostCarebear() || !PluginInfoUtils.getBooleanPluginInfoParam(pluginOption.get().getPluginInformation(), "server-licensing-enabled");
    }

    Iterable<Pair<String, UpmAppManager.ApplicationDescriptorModuleInfo>> getPluginsInApplication(com.atlassian.plugin.Plugin plugin) {
        return (Iterable)this.safeGetAndTransform(plugin, new Function<ModuleDescriptor<?>, Iterable<Pair<String, UpmAppManager.ApplicationDescriptorModuleInfo>>>(){

            public Iterable<Pair<String, UpmAppManager.ApplicationDescriptorModuleInfo>> apply(ModuleDescriptor<?> md) {
                ApplicationMetaDataModuleDescriptor amd = (ApplicationMetaDataModuleDescriptor)md;
                final String appKey = amd.getApplicationKey().value();
                return ImmutableList.copyOf((Iterable)Iterables.transform((Iterable)((PluginApplicationMetaData)amd.getModule()).getPlugins(), (Function)new Function<ApplicationPlugin, Pair<String, UpmAppManager.ApplicationDescriptorModuleInfo>>(){

                    public Pair<String, UpmAppManager.ApplicationDescriptorModuleInfo> apply(ApplicationPlugin p) {
                        UpmAppManager.ApplicationPluginType apt;
                        switch (p.getType()) {
                            case PRIMARY: {
                                apt = UpmAppManager.ApplicationPluginType.PRIMARY;
                                break;
                            }
                            case APPLICATION: {
                                apt = UpmAppManager.ApplicationPluginType.APPLICATION;
                                break;
                            }
                            default: {
                                apt = UpmAppManager.ApplicationPluginType.UTILITY;
                            }
                        }
                        return Pair.pair(p.getPluginKey(), new UpmAppManager.ApplicationDescriptorModuleInfo(appKey, apt));
                    }
                }));
            }
        }).getOrElse(Collections.emptyList());
    }

    private boolean isNotDataCenterBundleOnServer(String pluginKey) {
        return HostingType.DATA_CENTER.equals((Object)this.versionAwareHostApplicationInformation.getHostingType()) || this.notUsesLicensingOnServer(pluginKey);
    }

    private boolean notUsesLicensingOnServer(String pluginKey) {
        Option<Plugin> pluginOption = this.pluginRetriever.getPlugin(pluginKey);
        if (pluginOption.isDefined()) {
            return HostingType.SERVER.equals((Object)this.versionAwareHostApplicationInformation.getHostingType()) && !PluginInfoUtils.getBooleanPluginInfoParam(pluginOption.get().getPluginInformation(), "server-licensing-enabled");
        }
        return false;
    }

    private <T> Option<T> safeGetAndTransform(com.atlassian.plugin.Plugin plugin, Function<ModuleDescriptor<?>, T> f) {
        if (this.applicationDescriptorExists) {
            for (ModuleDescriptor md : plugin.getModuleDescriptors()) {
                if (!(md instanceof ApplicationMetaDataModuleDescriptor)) continue;
                return Option.some(f.apply((Object)md));
            }
        }
        return Option.none();
    }
}

