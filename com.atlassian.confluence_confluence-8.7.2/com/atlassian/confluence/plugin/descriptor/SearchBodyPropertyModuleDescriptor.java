/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.google.common.base.Strings
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.plugin.module.SearchBodyProperty;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.google.common.base.Strings;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class SearchBodyPropertyModuleDescriptor
extends AbstractModuleDescriptor<SearchBodyProperty> {
    private SearchBodyProperty searchBodyProperty;

    public SearchBodyPropertyModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.moduleClassName = SearchBodyProperty.class.getName();
        ContentType contentType = ContentType.valueOf((String)this.getAttribute(element, "content-type"));
        String contentProperty = this.getAttribute(element, "content-property");
        this.searchBodyProperty = new SearchBodyProperty(contentType, contentProperty);
    }

    private String getAttribute(Element element, String attributeName) throws PluginParseException {
        String attribute = element.attributeValue(attributeName);
        if (Strings.isNullOrEmpty((String)attribute)) {
            throw new PluginParseException(String.format("Module %s must define a \"%s\" attribute", this.getCompleteKey(), attributeName));
        }
        return attribute;
    }

    public SearchBodyProperty getModule() {
        return this.searchBodyProperty;
    }
}

