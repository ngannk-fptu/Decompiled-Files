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
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverter;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class AttachmentConverterModuleDescriptor
extends AbstractModuleDescriptor<AttachmentConverter<?>>
implements PluginModuleFactory<AttachmentConverter<?>>,
WeightedDescriptor {
    private PluginModuleHolder<AttachmentConverter<?>> holder = PluginModuleHolder.getInstance((PluginModuleFactory)this);
    private int weight;

    public AttachmentConverterModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.weight = Integer.parseInt(element.attributeValue("weight"));
    }

    public AttachmentConverter<?> getModule() {
        return (AttachmentConverter)this.holder.getModule();
    }

    public AttachmentConverter<?> createModule() {
        return (AttachmentConverter)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
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

