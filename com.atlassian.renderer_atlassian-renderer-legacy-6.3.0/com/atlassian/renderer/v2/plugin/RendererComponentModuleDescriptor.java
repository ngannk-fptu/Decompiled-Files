/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptor
 *  org.dom4j.Element
 */
package com.atlassian.renderer.v2.plugin;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import com.atlassian.renderer.v2.components.RendererComponent;
import com.atlassian.renderer.v2.plugin.RendererComponentFactory;
import org.dom4j.Element;

public class RendererComponentModuleDescriptor
extends AbstractModuleDescriptor
implements WeightedDescriptor {
    private RendererComponent component;
    private int weight;

    public RendererComponentModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.initialiseWeight(element);
    }

    public void enabled() {
        super.enabled();
        this.assertComponentOfExpectedClass();
    }

    public Object getModule() {
        if (this.component == null) {
            this.component = this.createComponent();
        }
        return this.component;
    }

    public int getWeight() {
        return this.weight;
    }

    protected Object instantiateComponentClass() {
        try {
            return this.getModuleClass().newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Could not create component: " + ((Object)((Object)this)).getClass() + ": " + e, e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Could not create component: " + ((Object)((Object)this)).getClass() + ": " + e, e);
        }
    }

    private RendererComponent createComponent() {
        Object o = this.instantiateComponentClass();
        if (o instanceof RendererComponent) {
            return (RendererComponent)o;
        }
        if (o instanceof RendererComponentFactory) {
            return ((RendererComponentFactory)o).getComponentInstance(this.getParams());
        }
        throw new IllegalStateException("Renderer component does not implement RendererComponent or RendererComponentFactory: " + o.getClass());
    }

    private void initialiseWeight(Element element) throws PluginParseException {
        String weightStr = element.attributeValue("weight");
        if (weightStr == null) {
            throw new PluginParseException("Renderer component plugins must specify a weight");
        }
        try {
            this.weight = Integer.parseInt(weightStr);
        }
        catch (NumberFormatException e) {
            throw new PluginParseException("Invalid weight, must be a number: " + weightStr);
        }
    }

    private void assertComponentOfExpectedClass() throws PluginParseException {
        Class moduleClass = this.getModuleClass();
        if (!RendererComponent.class.isAssignableFrom(moduleClass) && !RendererComponentFactory.class.isAssignableFrom(moduleClass)) {
            throw new PluginParseException("Module class must implement RendererComponent or RendererComponentFactory: " + moduleClass.getName());
        }
    }
}

