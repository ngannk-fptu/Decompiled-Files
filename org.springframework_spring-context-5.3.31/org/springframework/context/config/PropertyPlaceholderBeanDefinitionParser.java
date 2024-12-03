/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.springframework.context.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.config.AbstractPropertyLoadingBeanDefinitionParser;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

class PropertyPlaceholderBeanDefinitionParser
extends AbstractPropertyLoadingBeanDefinitionParser {
    private static final String SYSTEM_PROPERTIES_MODE_ATTRIBUTE = "system-properties-mode";
    private static final String SYSTEM_PROPERTIES_MODE_DEFAULT = "ENVIRONMENT";

    PropertyPlaceholderBeanDefinitionParser() {
    }

    protected Class<?> getBeanClass(Element element) {
        if (SYSTEM_PROPERTIES_MODE_DEFAULT.equals(element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIBUTE))) {
            return PropertySourcesPlaceholderConfigurer.class;
        }
        return PropertyPlaceholderConfigurer.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        builder.addPropertyValue("ignoreUnresolvablePlaceholders", (Object)Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
        String systemPropertiesModeName = element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIBUTE);
        if (StringUtils.hasLength((String)systemPropertiesModeName) && !systemPropertiesModeName.equals(SYSTEM_PROPERTIES_MODE_DEFAULT)) {
            builder.addPropertyValue("systemPropertiesModeName", (Object)("SYSTEM_PROPERTIES_MODE_" + systemPropertiesModeName));
        }
        if (element.hasAttribute("value-separator")) {
            builder.addPropertyValue("valueSeparator", (Object)element.getAttribute("value-separator"));
        }
        if (element.hasAttribute("trim-values")) {
            builder.addPropertyValue("trimValues", (Object)element.getAttribute("trim-values"));
        }
        if (element.hasAttribute("null-value")) {
            builder.addPropertyValue("nullValue", (Object)element.getAttribute("null-value"));
        }
    }
}

