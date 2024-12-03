/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jdbc.config.DatabasePopulatorConfigUtils;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

class EmbeddedDatabaseBeanDefinitionParser
extends AbstractBeanDefinitionParser {
    static final String DB_NAME_ATTRIBUTE = "database-name";
    static final String GENERATE_NAME_ATTRIBUTE = "generate-name";

    EmbeddedDatabaseBeanDefinitionParser() {
    }

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(EmbeddedDatabaseFactoryBean.class);
        this.setGenerateUniqueDatabaseNameFlag(element, builder);
        this.setDatabaseName(element, builder);
        this.setDatabaseType(element, builder);
        DatabasePopulatorConfigUtils.setDatabasePopulator(element, builder);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource((Object)element));
        return builder.getBeanDefinition();
    }

    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

    private void setGenerateUniqueDatabaseNameFlag(Element element, BeanDefinitionBuilder builder) {
        String generateName = element.getAttribute(GENERATE_NAME_ATTRIBUTE);
        if (StringUtils.hasText((String)generateName)) {
            builder.addPropertyValue("generateUniqueDatabaseName", (Object)generateName);
        }
    }

    private void setDatabaseName(Element element, BeanDefinitionBuilder builder) {
        String name = element.getAttribute(DB_NAME_ATTRIBUTE);
        if (!StringUtils.hasText((String)name)) {
            name = element.getAttribute("id");
        }
        if (StringUtils.hasText((String)name)) {
            builder.addPropertyValue("databaseName", (Object)name);
        }
    }

    private void setDatabaseType(Element element, BeanDefinitionBuilder builder) {
        String type = element.getAttribute("type");
        if (StringUtils.hasText((String)type)) {
            builder.addPropertyValue("databaseType", (Object)type);
        }
    }
}

