/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.rss.FeedCustomContentType;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class FeedCustomContentTypeModuleDescriptor
extends AbstractModuleDescriptor<FeedCustomContentType>
implements PluginModuleFactory<FeedCustomContentType> {
    private PluginModuleHolder<FeedCustomContentType> searchModule = PluginModuleHolder.getInstance(this);

    public FeedCustomContentTypeModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public FeedCustomContentType getModule() {
        return this.searchModule.getModule();
    }

    @Override
    public FeedCustomContentType createModule() {
        return (FeedCustomContentType)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
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

