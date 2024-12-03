/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.springframework.web.servlet.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class GroovyMarkupConfigurerBeanDefinitionParser
extends AbstractSimpleBeanDefinitionParser {
    public static final String BEAN_NAME = "mvcGroovyMarkupConfigurer";

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return BEAN_NAME;
    }

    protected String getBeanClassName(Element element) {
        return "org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer";
    }

    protected boolean isEligibleAttribute(String name) {
        return name.equals("auto-indent") || name.equals("cache-templates") || name.equals("resource-loader-path");
    }
}

