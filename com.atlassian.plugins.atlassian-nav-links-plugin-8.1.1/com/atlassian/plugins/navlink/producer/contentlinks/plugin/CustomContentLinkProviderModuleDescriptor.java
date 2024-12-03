/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkProvider
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.plugins.navlink.producer.contentlinks.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink.CustomContentLinkProvider;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class CustomContentLinkProviderModuleDescriptor
extends AbstractModuleDescriptor<CustomContentLinkProvider> {
    private CustomContentLinkProvider linkProviderInstance;

    public CustomContentLinkProviderModuleDescriptor(@Nonnull ModuleFactory moduleFactory) {
        super((ModuleFactory)Preconditions.checkNotNull((Object)moduleFactory));
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, (Element)Preconditions.checkNotNull((Object)element));
        String classAttr = element.attributeValue("class");
        if (StringUtils.isBlank((CharSequence)classAttr)) {
            throw new PluginParseException(String.format("Could not parse plugin module %s. Class attribute is mandatory and should not be blank", this.getPluginKey()));
        }
    }

    public CustomContentLinkProvider getModule() {
        return this.linkProviderInstance;
    }

    public void enabled() {
        super.enabled();
        this.linkProviderInstance = (CustomContentLinkProvider)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

