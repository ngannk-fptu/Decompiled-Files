/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.eclipse.gemini.blueprint.blueprint.compendium.cm.config;

import org.eclipse.gemini.blueprint.compendium.config.internal.ConfigPropertiesDefinitionParser;
import org.eclipse.gemini.blueprint.compendium.config.internal.ManagedPropertiesDefinitionParser;
import org.eclipse.gemini.blueprint.compendium.config.internal.ManagedServiceFactoryDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

class BlueprintCmNamespaceHandler
extends NamespaceHandlerSupport {
    static final String MANAGED_PROPS = "managed-properties";
    static final String MANAGED_FACTORY_PROPS = "managed-service-factory";
    static final String CM_PROPS = "cm-properties";

    BlueprintCmNamespaceHandler() {
    }

    public void init() {
        this.registerBeanDefinitionParser(CM_PROPS, (BeanDefinitionParser)new ConfigPropertiesDefinitionParser());
        this.registerBeanDefinitionParser(MANAGED_FACTORY_PROPS, (BeanDefinitionParser)new ManagedServiceFactoryDefinitionParser());
        this.registerBeanDefinitionDecorator(MANAGED_PROPS, new ManagedPropertiesDefinitionParser());
    }
}

