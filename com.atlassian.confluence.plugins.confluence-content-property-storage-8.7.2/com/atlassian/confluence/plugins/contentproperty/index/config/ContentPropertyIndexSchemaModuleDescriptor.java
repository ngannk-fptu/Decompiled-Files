/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.module.DefaultPluginModuleFactory
 *  com.atlassian.confluence.plugin.module.PluginModuleFactory
 *  com.atlassian.confluence.plugin.module.PluginModuleHolder
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugins.contentproperty.index.config;

import com.atlassian.confluence.plugin.module.DefaultPluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.plugins.contentproperty.index.config.XmlDescriptorSchemaReader;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertyIndexSchema;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class ContentPropertyIndexSchemaModuleDescriptor
extends AbstractModuleDescriptor<ContentPropertyIndexSchema> {
    private final PluginModuleHolder<ContentPropertyIndexSchema> moduleHolder = PluginModuleHolder.getInstance((PluginModuleFactory)new DefaultPluginModuleFactory<ContentPropertyIndexSchema>((ModuleDescriptor)this){

        public ContentPropertyIndexSchema createModule() {
            return ContentPropertyIndexSchemaModuleDescriptor.this.parsedIndexSchema;
        }
    });
    private final XmlDescriptorSchemaReader schemaReader = new XmlDescriptorSchemaReader();
    private ContentPropertyIndexSchema parsedIndexSchema;

    public ContentPropertyIndexSchemaModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        String moduleName = element.attributeValue("key");
        String pluginName = plugin.getName();
        this.parsedIndexSchema = new ContentPropertyIndexSchema(this.schemaReader.read(element, pluginName, moduleName).asMultimap());
    }

    public ContentPropertyIndexSchema getModule() {
        return (ContentPropertyIndexSchema)this.moduleHolder.getModule();
    }

    public void disabled() {
        super.disabled();
        this.moduleHolder.disabled();
    }

    public void enabled() {
        super.enabled();
        this.moduleHolder.enabled(this.getModuleClass());
    }

    protected String getModuleClassName() {
        return ContentPropertyIndexSchema.class.getName();
    }
}

