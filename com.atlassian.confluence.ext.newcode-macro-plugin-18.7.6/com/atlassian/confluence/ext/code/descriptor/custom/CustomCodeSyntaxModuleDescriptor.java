/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.ext.code.descriptor.custom;

import com.atlassian.confluence.ext.code.descriptor.custom.CustomCodeSyntax;
import com.atlassian.confluence.ext.code.descriptor.custom.CustomCodeSyntaxImpl;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.util.concurrent.ResettableLazyReference;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class CustomCodeSyntaxModuleDescriptor
extends AbstractModuleDescriptor<CustomCodeSyntax> {
    private String resourceKey;
    private String friendlyName;
    private final ResettableLazyReference<CustomCodeSyntax> module = new ResettableLazyReference<CustomCodeSyntax>(){

        protected CustomCodeSyntax create() {
            return new CustomCodeSyntaxImpl(CustomCodeSyntaxModuleDescriptor.this.resourceKey, CustomCodeSyntaxModuleDescriptor.this.friendlyName);
        }
    };

    public CustomCodeSyntaxModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.resourceKey = element.attributeValue("resource-key");
        this.friendlyName = element.attributeValue("friendly-name");
    }

    public CustomCodeSyntax getModule() {
        return (CustomCodeSyntax)this.module.get();
    }

    public String getModuleClassName() {
        return this.getModuleClass().getName();
    }

    public Class<CustomCodeSyntax> getModuleClass() {
        return CustomCodeSyntax.class;
    }
}

