/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.plugins.navlink.producer.navigation.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugins.navlink.producer.navigation.services.NavigationLinkRepository;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class NavigationLinksModuleDescriptor
extends AbstractModuleDescriptor<NavigationLinkRepository> {
    private String navigationLinkRepositoryClassName;
    private NavigationLinkRepository navigationLinkRepositoryInstance;

    public NavigationLinksModuleDescriptor(@Nonnull ModuleFactory moduleFactory) {
        super((ModuleFactory)Preconditions.checkNotNull((Object)moduleFactory));
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, (Element)Preconditions.checkNotNull((Object)element));
        this.navigationLinkRepositoryClassName = element.attributeValue("class");
        if (this.navigationLinkRepositoryClassName == null) {
            throw this.exceptionWhileParsing("class attribute is mandatory");
        }
        if (this.navigationLinkRepositoryClassName.trim().isEmpty()) {
            throw this.exceptionWhileParsing("class attribute must not be empty");
        }
    }

    public void enabled() {
        super.enabled();
        this.navigationLinkRepositoryInstance = (NavigationLinkRepository)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    @Nonnull
    public NavigationLinkRepository getModule() {
        return this.navigationLinkRepositoryInstance;
    }

    private PluginParseException exceptionWhileParsing(@Nonnull String reason) {
        return new PluginParseException(String.format("failed to parse plugin module %s; reason: %s", this.getCompleteKey(), reason));
    }
}

