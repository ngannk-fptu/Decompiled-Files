/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.springframework.ejb.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.ejb.config.AbstractJndiLocatingBeanDefinitionParser;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

class JndiLookupBeanDefinitionParser
extends AbstractJndiLocatingBeanDefinitionParser {
    public static final String DEFAULT_VALUE = "default-value";
    public static final String DEFAULT_REF = "default-ref";
    public static final String DEFAULT_OBJECT = "defaultObject";

    JndiLookupBeanDefinitionParser() {
    }

    protected Class<?> getBeanClass(Element element) {
        return JndiObjectFactoryBean.class;
    }

    @Override
    protected boolean isEligibleAttribute(String attributeName) {
        return super.isEligibleAttribute(attributeName) && !DEFAULT_VALUE.equals(attributeName) && !DEFAULT_REF.equals(attributeName);
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        String defaultValue = element.getAttribute(DEFAULT_VALUE);
        String defaultRef = element.getAttribute(DEFAULT_REF);
        if (StringUtils.hasLength((String)defaultValue)) {
            if (StringUtils.hasLength((String)defaultRef)) {
                parserContext.getReaderContext().error("<jndi-lookup> element is only allowed to contain either 'default-value' attribute OR 'default-ref' attribute, not both", (Object)element);
            }
            builder.addPropertyValue(DEFAULT_OBJECT, (Object)defaultValue);
        } else if (StringUtils.hasLength((String)defaultRef)) {
            builder.addPropertyValue(DEFAULT_OBJECT, (Object)new RuntimeBeanReference(defaultRef));
        }
    }
}

