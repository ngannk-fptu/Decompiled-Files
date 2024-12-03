/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.eclipse.gemini.blueprint.compendium.config;

import org.eclipse.gemini.blueprint.compendium.config.internal.ConfigPropertiesDefinitionParser;
import org.eclipse.gemini.blueprint.compendium.config.internal.ManagedPropertiesDefinitionParser;
import org.eclipse.gemini.blueprint.compendium.config.internal.ManagedServiceFactoryDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

class CompendiumNamespaceHandler
extends NamespaceHandlerSupport {
    CompendiumNamespaceHandler() {
    }

    public void init() {
        this.registerBeanDefinitionParser("cm-properties", (BeanDefinitionParser)new ConfigPropertiesDefinitionParser());
        this.registerBeanDefinitionParser("managed-service-factory", (BeanDefinitionParser)new ManagedServiceFactoryDefinitionParser());
        this.registerBeanDefinitionDecorator("managed-properties", new ManagedPropertiesDefinitionParser());
    }
}

