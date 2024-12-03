/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;

public abstract class BaseWeightedModuleDescriptor<T>
extends AbstractModuleDescriptor<T>
implements Comparable<BaseWeightedModuleDescriptor<T>> {
    private int weight;

    public BaseWeightedModuleDescriptor(ModuleFactory moduleFactory, int defaultWeight) {
        super(moduleFactory);
        this.weight = defaultWeight;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        Attribute weightAttr = element.attribute("weight");
        if (weightAttr != null) {
            try {
                this.weight = Integer.parseInt(weightAttr.getValue());
            }
            catch (NumberFormatException e) {
                throw new PluginParseException("The 'weight' attribute must be a number.", (Throwable)e);
            }
        }
    }

    @Override
    public int compareTo(BaseWeightedModuleDescriptor<T> otherDescriptor) {
        return this.weight - otherDescriptor.weight;
    }
}

