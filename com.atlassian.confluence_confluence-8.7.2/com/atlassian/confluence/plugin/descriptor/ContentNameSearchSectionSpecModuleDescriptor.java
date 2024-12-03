/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class ContentNameSearchSectionSpecModuleDescriptor
extends AbstractModuleDescriptor<ContentNameSearchSectionSpec>
implements PluginModuleFactory<ContentNameSearchSectionSpec> {
    private static final Logger log = LoggerFactory.getLogger(ContentNameSearchSectionSpecModuleDescriptor.class);
    private PluginModuleHolder<ContentNameSearchSectionSpec> module;

    public ContentNameSearchSectionSpecModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.module = PluginModuleHolder.getInstance(this);
    }

    public ContentNameSearchSectionSpec getModule() {
        return this.module.getModule();
    }

    public void enabled() {
        super.enabled();
        this.module.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.module.disabled();
        super.disabled();
    }

    @Override
    public ContentNameSearchSectionSpec createModule() {
        return (ContentNameSearchSectionSpec)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

