/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.springframework.jdbc.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jdbc.config.DatabasePopulatorConfigUtils;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.w3c.dom.Element;

class InitializeDatabaseBeanDefinitionParser
extends AbstractBeanDefinitionParser {
    InitializeDatabaseBeanDefinitionParser() {
    }

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceInitializer.class);
        builder.addPropertyReference("dataSource", element.getAttribute("data-source"));
        builder.addPropertyValue("enabled", (Object)element.getAttribute("enabled"));
        DatabasePopulatorConfigUtils.setDatabasePopulator(element, builder);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource((Object)element));
        return builder.getBeanDefinition();
    }

    protected boolean shouldGenerateId() {
        return true;
    }
}

