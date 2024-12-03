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
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class VelocityContextItemModuleDescriptor
extends AbstractModuleDescriptor<Object>
implements PluginModuleFactory<Object> {
    private String contextKey;
    private PluginModuleHolder<Object> module;

    public VelocityContextItemModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.contextKey = element.attributeValue("context-key");
        if (StringUtils.isBlank((CharSequence)this.contextKey)) {
            throw new PluginParseException("Module " + this.getCompleteKey() + " must define a \"context-key\" attribute");
        }
        this.module = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public Object getModule() {
        return this.createModule();
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
    public Object createModule() {
        return ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
    }

    public String getContextKey() {
        return this.contextKey;
    }
}

