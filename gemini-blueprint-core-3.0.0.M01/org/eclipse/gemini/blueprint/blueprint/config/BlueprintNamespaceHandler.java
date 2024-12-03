/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.eclipse.gemini.blueprint.blueprint.config;

import org.eclipse.gemini.blueprint.blueprint.config.BlueprintBeanBeanDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.BlueprintBeanDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.TypeConverterBeanDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintCollectionBeanDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintReferenceBeanDefinitionParser;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintServiceDefinitionParser;
import org.eclipse.gemini.blueprint.service.importer.support.CollectionType;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

class BlueprintNamespaceHandler
extends NamespaceHandlerSupport {
    BlueprintNamespaceHandler() {
    }

    public void init() {
        this.registerBeanDefinitionParser("blueprint", new BlueprintBeanDefinitionParser());
        this.registerBeanDefinitionParser("bean", new BlueprintBeanBeanDefinitionParser());
        this.registerBeanDefinitionParser("type-converters", (BeanDefinitionParser)new TypeConverterBeanDefinitionParser());
        this.registerBeanDefinitionParser("reference", (BeanDefinitionParser)new BlueprintReferenceBeanDefinitionParser());
        this.registerBeanDefinitionParser("reference-list", (BeanDefinitionParser)new BlueprintCollectionBeanDefinitionParser(){

            @Override
            protected CollectionType collectionType() {
                return CollectionType.LIST;
            }
        });
        this.registerBeanDefinitionParser("reference-set", (BeanDefinitionParser)new BlueprintCollectionBeanDefinitionParser(){

            @Override
            protected CollectionType collectionType() {
                return CollectionType.SET;
            }
        });
        this.registerBeanDefinitionParser("service", (BeanDefinitionParser)new BlueprintServiceDefinitionParser());
    }
}

