/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.servlet.simpledisplay.PathConverter;
import com.atlassian.confluence.servlet.simpledisplay.PathConverterManager;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class PathConverterModuleDescriptor
extends AbstractModuleDescriptor<PathConverter> {
    private static final int DEFAULT_WEIGHT = 100;
    private PluginModuleHolder<PathConverter> moduleHolder;
    private final PathConverterManager pathConverterManager;
    private int weight;

    public PathConverterModuleDescriptor(ModuleFactory moduleFactory, PathConverterManager pathConverterManager) {
        super(moduleFactory);
        this.pathConverterManager = pathConverterManager;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.setWeight(PathConverterModuleDescriptor.calculateWeight(element));
        this.setModuleHolder(PluginModuleHolder.getInstanceWithDefaultFactory(this));
    }

    public PathConverter getModule() {
        return this.moduleHolder.getModule();
    }

    public void enabled() {
        super.enabled();
        this.moduleHolder.enabled(this.getModuleClass());
        this.pathConverterManager.addPathConverter(this.weight, this.moduleHolder.getModule());
    }

    public void disabled() {
        if (this.moduleHolder.isEnabled()) {
            this.pathConverterManager.removePathConverter(this.moduleHolder.getModule());
        }
        this.moduleHolder.disabled();
        super.disabled();
    }

    private static int calculateWeight(Element element) {
        String value;
        Attribute att = element.attribute("weight");
        if (att != null && StringUtils.isNotBlank((CharSequence)(value = att.getValue()))) {
            return Integer.parseInt(value);
        }
        return 100;
    }

    void setModuleHolder(PluginModuleHolder<PathConverter> moduleHolder) {
        this.moduleHolder = moduleHolder;
    }

    void setWeight(int weight) {
        this.weight = weight;
    }
}

