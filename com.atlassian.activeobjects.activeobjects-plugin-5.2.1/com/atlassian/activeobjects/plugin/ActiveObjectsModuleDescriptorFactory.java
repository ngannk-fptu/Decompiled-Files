/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.plugin;

import com.atlassian.activeobjects.EntitiesValidator;
import com.atlassian.activeobjects.admin.PluginToTablesMapping;
import com.atlassian.activeobjects.config.ActiveObjectsConfigurationFactory;
import com.atlassian.activeobjects.osgi.OsgiServiceUtils;
import com.atlassian.activeobjects.plugin.ActiveObjectModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.google.common.base.Preconditions;

public final class ActiveObjectsModuleDescriptorFactory
extends SingleModuleDescriptorFactory<ActiveObjectModuleDescriptor> {
    private final ModuleFactory moduleFactory;
    private final OsgiServiceUtils osgiUtils;
    private final PluginToTablesMapping pluginToTablesMapping;
    private final EntitiesValidator entitiesValidator;
    private final ActiveObjectsConfigurationFactory configurationFactory;

    public ActiveObjectsModuleDescriptorFactory(ModuleFactory moduleFactory, HostContainer hostContainer, ActiveObjectsConfigurationFactory configurationFactory, OsgiServiceUtils osgiUtils, PluginToTablesMapping pluginToTablesMapping, EntitiesValidator entitiesValidator) {
        super((HostContainer)Preconditions.checkNotNull((Object)hostContainer), "ao", ActiveObjectModuleDescriptor.class);
        this.moduleFactory = (ModuleFactory)Preconditions.checkNotNull((Object)moduleFactory);
        this.configurationFactory = (ActiveObjectsConfigurationFactory)Preconditions.checkNotNull((Object)configurationFactory);
        this.osgiUtils = (OsgiServiceUtils)Preconditions.checkNotNull((Object)osgiUtils);
        this.pluginToTablesMapping = (PluginToTablesMapping)Preconditions.checkNotNull((Object)pluginToTablesMapping);
        this.entitiesValidator = (EntitiesValidator)Preconditions.checkNotNull((Object)entitiesValidator);
    }

    public ModuleDescriptor getModuleDescriptor(String type) {
        return this.hasModuleDescriptor(type) ? new ActiveObjectModuleDescriptor(this.moduleFactory, this.configurationFactory, this.osgiUtils, this.pluginToTablesMapping, this.entitiesValidator) : null;
    }
}

