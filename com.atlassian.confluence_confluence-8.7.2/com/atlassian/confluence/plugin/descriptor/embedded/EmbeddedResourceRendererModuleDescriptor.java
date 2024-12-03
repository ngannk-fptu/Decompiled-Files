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
package com.atlassian.confluence.plugin.descriptor.embedded;

import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;

public class EmbeddedResourceRendererModuleDescriptor
extends AbstractModuleDescriptor<EmbeddedResourceRenderer> {
    private PluginModuleHolder<EmbeddedResourceRenderer> embeddedResourceRendererStore;

    public EmbeddedResourceRendererModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.embeddedResourceRendererStore = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public EmbeddedResourceRenderer getModule() {
        return this.embeddedResourceRendererStore.getModule();
    }

    public void enabled() {
        super.enabled();
        this.embeddedResourceRendererStore.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.embeddedResourceRendererStore.disabled();
        super.disabled();
    }
}

