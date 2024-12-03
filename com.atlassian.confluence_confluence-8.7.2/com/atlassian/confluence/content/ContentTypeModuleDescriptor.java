/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.custom.CustomContentType;
import com.atlassian.confluence.content.custom.CustomContentTypeWrapper;
import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class ContentTypeModuleDescriptor
extends AbstractModuleDescriptor<ContentType>
implements PluginModuleFactory<ContentType> {
    private PluginModuleHolder<ContentType> module;
    private ApiSupportProvider apiSupportProvider;

    public ContentTypeModuleDescriptor(ModuleFactory moduleFactory, ApiSupportProvider apiSupportProvider) {
        super(moduleFactory);
        this.apiSupportProvider = apiSupportProvider;
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.module = PluginModuleHolder.getInstance(this);
    }

    public void enabled() {
        super.enabled();
        this.module.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.module.disabled();
        super.disabled();
    }

    public ContentType getModule() {
        ContentType type = this.module.getModule();
        if (type instanceof CustomContentType) {
            return type;
        }
        return new CustomContentTypeWrapper(type, com.atlassian.confluence.api.model.content.ContentType.valueOf((String)this.getCompleteKey()), this.apiSupportProvider);
    }

    @Override
    public ContentType createModule() {
        return (ContentType)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
    }

    public String getContentType() {
        return this.getCompleteKey();
    }
}

