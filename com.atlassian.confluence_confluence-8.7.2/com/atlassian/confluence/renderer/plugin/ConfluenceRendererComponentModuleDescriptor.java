/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.renderer.v2.components.RendererComponent
 *  com.atlassian.renderer.v2.plugin.RendererComponentModuleDescriptor
 *  org.dom4j.Element
 */
package com.atlassian.confluence.renderer.plugin;

import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.renderer.v2.components.RendererComponent;
import com.atlassian.renderer.v2.plugin.RendererComponentModuleDescriptor;
import org.dom4j.Element;

public class ConfluenceRendererComponentModuleDescriptor<T extends RendererComponent>
extends RendererComponentModuleDescriptor {
    private PluginModuleHolder<?> rendererHolder;

    public ConfluenceRendererComponentModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.rendererHolder = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public void enabled() {
        super.enabled();
        this.rendererHolder.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.rendererHolder.disabled();
        super.disabled();
    }

    protected Object instantiateComponentClass() {
        return this.rendererHolder.getModule();
    }
}

