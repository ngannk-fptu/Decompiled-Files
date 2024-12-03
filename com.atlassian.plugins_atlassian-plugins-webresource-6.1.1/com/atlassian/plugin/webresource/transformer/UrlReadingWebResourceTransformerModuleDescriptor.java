/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory
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
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory;
import io.atlassian.util.concurrent.ResettableLazyReference;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class UrlReadingWebResourceTransformerModuleDescriptor
extends AbstractModuleDescriptor<WebResourceTransformerFactory> {
    private final ResettableLazyReference<WebResourceTransformerFactory> moduleLazyReference = new ResettableLazyReference<WebResourceTransformerFactory>(){

        protected WebResourceTransformerFactory create() throws Exception {
            return (WebResourceTransformerFactory)UrlReadingWebResourceTransformerModuleDescriptor.this.moduleFactory.createModule(UrlReadingWebResourceTransformerModuleDescriptor.this.moduleClassName, (ModuleDescriptor)UrlReadingWebResourceTransformerModuleDescriptor.this);
        }
    };
    private String aliasKey;

    public UrlReadingWebResourceTransformerModuleDescriptor(ModuleFactory moduleFactory) {
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

    public WebResourceTransformerFactory getModule() {
        return (WebResourceTransformerFactory)this.moduleLazyReference.get();
    }

    public String getAliasKey() {
        return this.aliasKey;
    }
}

