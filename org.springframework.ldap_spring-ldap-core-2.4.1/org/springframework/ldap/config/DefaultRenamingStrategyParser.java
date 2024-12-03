/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.springframework.ldap.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.ldap.config.ParserUtils;
import org.springframework.ldap.transaction.compensating.support.DefaultTempEntryRenamingStrategy;
import org.w3c.dom.Element;

public class DefaultRenamingStrategyParser
implements BeanDefinitionParser {
    private static final String ATT_TEMP_SUFFIX = "temp-suffix";

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DefaultTempEntryRenamingStrategy.class);
        builder.addPropertyValue("tempSuffix", (Object)ParserUtils.getString(element, ATT_TEMP_SUFFIX, "_temp"));
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        parserContext.getContainingBeanDefinition().getPropertyValues().addPropertyValue("renamingStrategy", (Object)beanDefinition);
        return beanDefinition;
    }
}

