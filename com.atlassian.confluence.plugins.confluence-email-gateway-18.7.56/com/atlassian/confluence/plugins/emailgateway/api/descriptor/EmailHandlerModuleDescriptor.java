/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.module.PluginModuleFactory
 *  com.atlassian.confluence.plugin.module.PluginModuleHolder
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptor
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugins.emailgateway.api.descriptor;

import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHandler;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class EmailHandlerModuleDescriptor
extends AbstractModuleDescriptor<EmailHandler>
implements PluginModuleFactory<EmailHandler>,
WeightedDescriptor {
    private PluginModuleHolder<EmailHandler> holder = PluginModuleHolder.getInstance((PluginModuleFactory)this);
    private int weight;

    public EmailHandlerModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.weight = Integer.parseInt(element.attributeValue("weight"));
    }

    public EmailHandler getModule() {
        return (EmailHandler)this.holder.getModule();
    }

    public EmailHandler createModule() {
        return (EmailHandler)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public void enabled() {
        super.enabled();
        this.holder.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.holder.disabled();
        super.disabled();
    }

    public int getWeight() {
        return this.weight;
    }
}

