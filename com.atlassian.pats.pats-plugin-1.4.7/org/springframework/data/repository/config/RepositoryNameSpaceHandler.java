/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.data.repository.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.data.repository.config.ResourceReaderRepositoryPopulatorBeanDefinitionParser;

public class RepositoryNameSpaceHandler
extends NamespaceHandlerSupport {
    private static final BeanDefinitionParser PARSER = new ResourceReaderRepositoryPopulatorBeanDefinitionParser();

    public void init() {
        this.registerBeanDefinitionParser("unmarshaller-populator", PARSER);
        this.registerBeanDefinitionParser("jackson-populator", PARSER);
        this.registerBeanDefinitionParser("jackson2-populator", PARSER);
    }
}

