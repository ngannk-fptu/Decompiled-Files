/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.spi;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.streams.spi.StreamsActivityProvider;
import com.atlassian.streams.spi.StreamsCommentHandler;
import com.atlassian.streams.spi.StreamsEntityAssociationProvider;
import com.atlassian.streams.spi.StreamsFilterOptionProvider;
import com.atlassian.streams.spi.StreamsKeyProvider;
import com.atlassian.streams.spi.StreamsValidator;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ActivityProviderModuleDescriptor
extends AbstractModuleDescriptor<StreamsActivityProvider> {
    private final Logger logger = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private StreamsActivityProvider provider;
    private String commentHandlerClassName;
    private StreamsCommentHandler commentHandler;
    private String filterOptionProviderClassName;
    private StreamsFilterOptionProvider filterOptionProvider;
    private String entityAssociationProviderClassName;
    private StreamsEntityAssociationProvider entityAssociationProvider;
    private String keyProviderClassName;
    private StreamsKeyProvider keyProvider;
    private String validatorClassName;
    private StreamsValidator validator;

    public ActivityProviderModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.commentHandlerClassName = this.classAttribute(element.element("comment-handler"));
        this.filterOptionProviderClassName = this.classAttribute(element.element("filter-provider"));
        this.entityAssociationProviderClassName = this.classAttribute(element.element("entity-association-provider"));
        this.keyProviderClassName = this.classAttribute(element.element("key-provider"));
        this.validatorClassName = this.classAttribute(element.element("validator"));
    }

    private String classAttribute(Element element) {
        if (element == null) {
            return null;
        }
        return element.attributeValue("class");
    }

    public synchronized void enabled() {
        super.enabled();
        this.initModules();
    }

    public StreamsActivityProvider getModule() {
        this.initModules();
        return this.provider;
    }

    public StreamsCommentHandler getCommentHandler() {
        this.initModules();
        return this.commentHandler;
    }

    public StreamsFilterOptionProvider getFilterOptionProvider() {
        this.initModules();
        return this.filterOptionProvider;
    }

    public StreamsEntityAssociationProvider getEntityAssociationProvider() {
        this.initModules();
        return this.entityAssociationProvider;
    }

    public StreamsKeyProvider getKeyProvider() {
        this.initModules();
        return this.keyProvider;
    }

    public StreamsValidator getValidator() {
        this.initModules();
        return this.validator;
    }

    public synchronized void disabled() {
        super.disabled();
        this.disable(this.provider, this.commentHandler, this.filterOptionProvider, this.keyProvider, this.validator);
        this.provider = null;
        this.commentHandler = null;
        this.filterOptionProvider = null;
        this.keyProvider = null;
        this.validator = null;
    }

    private void initModules() {
        if (this.provider != null) {
            return;
        }
        this.logger.debug("Initialising activity provider '{}' defined as module '{}'", (Object)this.moduleClassName, (Object)this.getCompleteKey());
        this.provider = (StreamsActivityProvider)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
        this.commentHandler = this.newInstance(this.loadSubModuleClass("comment-handler", this.commentHandlerClassName, StreamsCommentHandler.class));
        this.filterOptionProvider = this.newInstance(this.loadSubModuleClass("filter-provider", this.filterOptionProviderClassName, StreamsFilterOptionProvider.class));
        this.entityAssociationProvider = this.newInstance(this.loadSubModuleClass("entity-association-provider", this.entityAssociationProviderClassName, StreamsEntityAssociationProvider.class));
        this.keyProvider = this.newInstance(this.loadSubModuleClass("key-provider", this.keyProviderClassName, StreamsKeyProvider.class));
        this.validator = this.newInstance(this.loadSubModuleClass("validator", this.validatorClassName, StreamsValidator.class));
    }

    private void disable(Object ... os) {
        for (Object o : os) {
            if (o == null || !(o instanceof StateAware)) continue;
            ((StateAware)o).disabled();
        }
    }

    private <A> Class<? extends A> loadSubModuleClass(String subModuleName, String subModuleClassName, Class<A> subModuleClassParentType) {
        if (subModuleClassName == null) {
            return null;
        }
        try {
            Class subModuleClass = this.plugin.loadClass(subModuleClassName, ((Object)((Object)this)).getClass());
            if (!subModuleClassParentType.isAssignableFrom(subModuleClass)) {
                throw new IllegalArgumentException("Sub module '" + subModuleName + "' class '" + subModuleClassName + "' must be of type '" + subModuleClassParentType.getName() + "'");
            }
            return subModuleClass;
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Sub module '" + subModuleName + "' class '" + subModuleClassName + "' not found ");
        }
    }

    private <T> T newInstance(Class<T> type) {
        if (type == null) {
            return null;
        }
        Object instance = ((ContainerManagedPlugin)this.plugin).getContainerAccessor().createBean(type);
        if (instance instanceof StateAware) {
            ((StateAware)instance).enabled();
        }
        return (T)instance;
    }
}

