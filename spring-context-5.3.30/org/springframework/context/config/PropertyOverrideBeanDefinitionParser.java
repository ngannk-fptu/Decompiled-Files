/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.PropertyOverrideConfigurer
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.springframework.context.config;

import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.config.AbstractPropertyLoadingBeanDefinitionParser;
import org.w3c.dom.Element;

class PropertyOverrideBeanDefinitionParser
extends AbstractPropertyLoadingBeanDefinitionParser {
    PropertyOverrideBeanDefinitionParser() {
    }

    protected Class<?> getBeanClass(Element element) {
        return PropertyOverrideConfigurer.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        builder.addPropertyValue("ignoreInvalidKeys", (Object)Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
    }
}

