/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.module.PluginModuleFactory
 *  com.atlassian.confluence.plugin.module.PluginModuleHolder
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.createcontent.extensions;

import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.plugins.createcontent.extensions.SpaceCreationStep;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

public class SpaceCreationStepDescriptor
extends AbstractModuleDescriptor<SpaceCreationStep>
implements PluginModuleFactory<SpaceCreationStep> {
    private PluginModuleHolder<SpaceCreationStep> pluginModuleHolder = PluginModuleHolder.getInstance((PluginModuleFactory)this);

    public SpaceCreationStepDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public SpaceCreationStep getModule() {
        return (SpaceCreationStep)this.pluginModuleHolder.getModule();
    }

    public SpaceCreationStep createModule() {
        return (SpaceCreationStep)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public void enabled() {
        super.enabled();
        this.pluginModuleHolder.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.pluginModuleHolder.disabled();
        super.disabled();
    }
}

