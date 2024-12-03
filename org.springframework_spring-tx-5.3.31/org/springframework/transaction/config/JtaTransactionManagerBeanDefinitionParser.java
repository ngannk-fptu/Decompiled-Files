/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.springframework.transaction.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.config.JtaTransactionManagerFactoryBean;
import org.w3c.dom.Element;

public class JtaTransactionManagerBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    protected String getBeanClassName(Element element) {
        return JtaTransactionManagerFactoryBean.resolveJtaTransactionManagerClassName();
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return "transactionManager";
    }
}

