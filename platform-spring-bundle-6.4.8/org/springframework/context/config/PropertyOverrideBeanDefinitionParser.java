/*
 * Decompiled with CFR 0.152.
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

    @Override
    protected Class<?> getBeanClass(Element element) {
        return PropertyOverrideConfigurer.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        builder.addPropertyValue("ignoreInvalidKeys", Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
    }
}

