/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.transformer.ContentTransformerFactory;
import io.atlassian.util.concurrent.ResettableLazyReference;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class ContentTransformerModuleDescriptor
extends AbstractModuleDescriptor<ContentTransformerFactory> {
    private final ResettableLazyReference<ContentTransformerFactory> moduleLazyReference = new ResettableLazyReference<ContentTransformerFactory>(){

        protected ContentTransformerFactory create() throws Exception {
            return (ContentTransformerFactory)ContentTransformerModuleDescriptor.this.moduleFactory.createModule(ContentTransformerModuleDescriptor.this.moduleClassName, (ModuleDescriptor)ContentTransformerModuleDescriptor.this);
        }
    };
    private String aliasKey;

    public ContentTransformerModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        this.aliasKey = element.attributeValue("alias-key");
        super.init(plugin, element);
    }

    public void disabled() {
        this.moduleLazyReference.reset();
        super.disabled();
    }

    public ContentTransformerFactory getModule() {
        return (ContentTransformerFactory)this.moduleLazyReference.get();
    }

    public String getAliasKey() {
        return this.aliasKey;
    }
}

