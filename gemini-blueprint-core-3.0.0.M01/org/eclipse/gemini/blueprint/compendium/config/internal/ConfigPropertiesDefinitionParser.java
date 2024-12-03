/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.eclipse.gemini.blueprint.compendium.config.internal;

import java.util.Properties;
import org.eclipse.gemini.blueprint.compendium.cm.ConfigAdminPropertiesFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ConfigPropertiesDefinitionParser
extends AbstractSimpleBeanDefinitionParser {
    private static final String PROPERTIES_PROP = "properties";

    protected Class<?> getBeanClass(Element element) {
        return ConfigAdminPropertiesFactoryBean.class;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        Properties parsedProps = parserContext.getDelegate().parsePropsElement(element);
        if (!parsedProps.isEmpty()) {
            if (builder.getRawBeanDefinition().getPropertyValues().contains(PROPERTIES_PROP)) {
                parserContext.getReaderContext().error("Property 'properties' is defined more then once. Only one approach may be used per property.", (Object)element);
            }
            builder.addPropertyValue(PROPERTIES_PROP, (Object)parsedProps);
        }
    }
}

