/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.ejb.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

abstract class AbstractJndiLocatingBeanDefinitionParser
extends AbstractSimpleBeanDefinitionParser {
    public static final String ENVIRONMENT = "environment";
    public static final String ENVIRONMENT_REF = "environment-ref";
    public static final String JNDI_ENVIRONMENT = "jndiEnvironment";

    AbstractJndiLocatingBeanDefinitionParser() {
    }

    protected boolean isEligibleAttribute(String attributeName) {
        return super.isEligibleAttribute(attributeName) && !ENVIRONMENT_REF.equals(attributeName) && !"lazy-init".equals(attributeName);
    }

    protected void postProcess(BeanDefinitionBuilder definitionBuilder, Element element) {
        String envValue = DomUtils.getChildElementValueByTagName((Element)element, (String)ENVIRONMENT);
        if (envValue != null) {
            definitionBuilder.addPropertyValue(JNDI_ENVIRONMENT, (Object)envValue);
        } else {
            String envRef = element.getAttribute(ENVIRONMENT_REF);
            if (StringUtils.hasLength((String)envRef)) {
                definitionBuilder.addPropertyValue(JNDI_ENVIRONMENT, (Object)new RuntimeBeanReference(envRef));
            }
        }
        String lazyInit = element.getAttribute("lazy-init");
        if (StringUtils.hasText((String)lazyInit) && !"default".equals(lazyInit)) {
            definitionBuilder.setLazyInit("true".equals(lazyInit));
        }
    }
}

