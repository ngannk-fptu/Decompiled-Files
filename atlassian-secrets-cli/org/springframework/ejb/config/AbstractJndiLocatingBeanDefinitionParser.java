/*
 * Decompiled with CFR 0.152.
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

    @Override
    protected boolean isEligibleAttribute(String attributeName) {
        return super.isEligibleAttribute(attributeName) && !ENVIRONMENT_REF.equals(attributeName) && !"lazy-init".equals(attributeName);
    }

    @Override
    protected void postProcess(BeanDefinitionBuilder definitionBuilder, Element element) {
        String envValue = DomUtils.getChildElementValueByTagName(element, ENVIRONMENT);
        if (envValue != null) {
            definitionBuilder.addPropertyValue(JNDI_ENVIRONMENT, envValue);
        } else {
            String envRef = element.getAttribute(ENVIRONMENT_REF);
            if (StringUtils.hasLength(envRef)) {
                definitionBuilder.addPropertyValue(JNDI_ENVIRONMENT, new RuntimeBeanReference(envRef));
            }
        }
        String lazyInit = element.getAttribute("lazy-init");
        if (StringUtils.hasText(lazyInit) && !"default".equals(lazyInit)) {
            definitionBuilder.setLazyInit("true".equals(lazyInit));
        }
    }
}

