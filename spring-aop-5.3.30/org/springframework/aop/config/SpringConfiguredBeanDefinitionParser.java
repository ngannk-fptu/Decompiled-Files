/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

class SpringConfiguredBeanDefinitionParser
implements BeanDefinitionParser {
    public static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME = "org.springframework.context.config.internalBeanConfigurerAspect";
    private static final String BEAN_CONFIGURER_ASPECT_CLASS_NAME = "org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect";

    SpringConfiguredBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition(BEAN_CONFIGURER_ASPECT_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClassName(BEAN_CONFIGURER_ASPECT_CLASS_NAME);
            def.setFactoryMethodName("aspectOf");
            def.setRole(2);
            def.setSource(parserContext.extractSource((Object)element));
            parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)def, BEAN_CONFIGURER_ASPECT_BEAN_NAME));
        }
        return null;
    }
}

