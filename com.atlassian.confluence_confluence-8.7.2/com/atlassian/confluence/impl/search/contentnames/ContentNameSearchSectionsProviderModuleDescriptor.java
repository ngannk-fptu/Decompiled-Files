/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 */
package com.atlassian.confluence.impl.search.contentnames;

import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionsProvider;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;

public class ContentNameSearchSectionsProviderModuleDescriptor
extends AbstractModuleDescriptor<ContentNameSearchSectionsProvider> {
    private PluginModuleHolder<ContentNameSearchSectionsProvider> sectionsProviderPluginModuleHolder;

    public ContentNameSearchSectionsProviderModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.sectionsProviderPluginModuleHolder = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public ContentNameSearchSectionsProvider getModule() {
        return this.sectionsProviderPluginModuleHolder.getModule();
    }

    public void enabled() {
        super.enabled();
        this.sectionsProviderPluginModuleHolder.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.sectionsProviderPluginModuleHolder.disabled();
        super.disabled();
    }
}

