/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.search.plugin;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.search.plugin.SiteSearchPluginModule;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class SiteSearchPluginModuleDescriptor
extends AbstractModuleDescriptor<SiteSearchPluginModule>
implements PluginModuleFactory<SiteSearchPluginModule> {
    private PluginModuleHolder<SiteSearchPluginModule> searchModule = PluginModuleHolder.getInstance(this);

    public SiteSearchPluginModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public SiteSearchPluginModule getModule() {
        return this.searchModule.getModule();
    }

    @Override
    public SiteSearchPluginModule createModule() {
        return (SiteSearchPluginModule)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
    }

    public void enabled() {
        super.enabled();
        this.searchModule.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.searchModule.disabled();
        super.disabled();
    }
}

