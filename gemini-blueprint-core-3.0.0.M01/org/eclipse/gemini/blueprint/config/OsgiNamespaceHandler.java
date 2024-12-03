/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.eclipse.gemini.blueprint.config;

import org.eclipse.gemini.blueprint.config.internal.BundleBeanDefinitionParser;
import org.eclipse.gemini.blueprint.config.internal.CollectionBeanDefinitionParser;
import org.eclipse.gemini.blueprint.config.internal.ReferenceBeanDefinitionParser;
import org.eclipse.gemini.blueprint.config.internal.ServiceBeanDefinitionParser;
import org.eclipse.gemini.blueprint.service.importer.support.CollectionType;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

class OsgiNamespaceHandler
extends NamespaceHandlerSupport {
    OsgiNamespaceHandler() {
    }

    public void init() {
        this.registerBeanDefinitionParser("reference", (BeanDefinitionParser)new ReferenceBeanDefinitionParser());
        this.registerBeanDefinitionParser("list", (BeanDefinitionParser)new CollectionBeanDefinitionParser(){

            @Override
            protected CollectionType collectionType() {
                return CollectionType.LIST;
            }
        });
        this.registerBeanDefinitionParser("set", (BeanDefinitionParser)new CollectionBeanDefinitionParser(){

            @Override
            protected CollectionType collectionType() {
                return CollectionType.SET;
            }
        });
        this.registerBeanDefinitionParser("service", (BeanDefinitionParser)new ServiceBeanDefinitionParser());
        this.registerBeanDefinitionParser("bundle", (BeanDefinitionParser)new BundleBeanDefinitionParser());
    }
}

