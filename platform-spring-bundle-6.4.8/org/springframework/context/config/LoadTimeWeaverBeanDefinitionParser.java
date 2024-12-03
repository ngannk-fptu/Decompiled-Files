/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.config;

import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.config.SpringConfiguredBeanDefinitionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

class LoadTimeWeaverBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    public static final String ASPECTJ_WEAVING_ENABLER_BEAN_NAME = "org.springframework.context.config.internalAspectJWeavingEnabler";
    private static final String ASPECTJ_WEAVING_ENABLER_CLASS_NAME = "org.springframework.context.weaving.AspectJWeavingEnabler";
    private static final String DEFAULT_LOAD_TIME_WEAVER_CLASS_NAME = "org.springframework.context.weaving.DefaultContextLoadTimeWeaver";
    private static final String WEAVER_CLASS_ATTRIBUTE = "weaver-class";
    private static final String ASPECTJ_WEAVING_ATTRIBUTE = "aspectj-weaving";

    LoadTimeWeaverBeanDefinitionParser() {
    }

    @Override
    protected String getBeanClassName(Element element) {
        if (element.hasAttribute(WEAVER_CLASS_ATTRIBUTE)) {
            return element.getAttribute(WEAVER_CLASS_ATTRIBUTE);
        }
        return DEFAULT_LOAD_TIME_WEAVER_CLASS_NAME;
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return "loadTimeWeaver";
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setRole(2);
        if (this.isAspectJWeavingEnabled(element.getAttribute(ASPECTJ_WEAVING_ATTRIBUTE), parserContext)) {
            if (!parserContext.getRegistry().containsBeanDefinition(ASPECTJ_WEAVING_ENABLER_BEAN_NAME)) {
                RootBeanDefinition def = new RootBeanDefinition(ASPECTJ_WEAVING_ENABLER_CLASS_NAME);
                parserContext.registerBeanComponent(new BeanComponentDefinition(def, ASPECTJ_WEAVING_ENABLER_BEAN_NAME));
            }
            if (this.isBeanConfigurerAspectEnabled(parserContext.getReaderContext().getBeanClassLoader())) {
                new SpringConfiguredBeanDefinitionParser().parse(element, parserContext);
            }
        }
    }

    protected boolean isAspectJWeavingEnabled(String value, ParserContext parserContext) {
        if ("on".equals(value)) {
            return true;
        }
        if ("off".equals(value)) {
            return false;
        }
        ClassLoader cl = parserContext.getReaderContext().getBeanClassLoader();
        return cl != null && cl.getResource("META-INF/aop.xml") != null;
    }

    protected boolean isBeanConfigurerAspectEnabled(@Nullable ClassLoader beanClassLoader) {
        return ClassUtils.isPresent("org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect", beanClassLoader);
    }
}

