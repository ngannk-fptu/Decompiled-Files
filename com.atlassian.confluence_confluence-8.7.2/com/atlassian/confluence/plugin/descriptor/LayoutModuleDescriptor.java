/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.themes.ThemedDecorator;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class LayoutModuleDescriptor
extends AbstractModuleDescriptor<ThemedDecorator> {
    private String decoratorPath;

    public LayoutModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.decoratorPath = element.attributeValue("overrides");
        if (StringUtils.isBlank((CharSequence)this.decoratorPath)) {
            throw new PluginParseException("Module " + this.getCompleteKey() + " must define an \"overrides\" attribute");
        }
    }

    public ThemedDecorator getModule() {
        ThemedDecorator themedDecorator = (ThemedDecorator)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
        themedDecorator.init(this);
        return themedDecorator;
    }

    public String getDecoratorPath() {
        return this.decoratorPath;
    }
}

