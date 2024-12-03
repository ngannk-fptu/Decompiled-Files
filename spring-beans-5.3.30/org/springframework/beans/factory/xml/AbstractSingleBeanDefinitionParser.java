/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;

public abstract class AbstractSingleBeanDefinitionParser
extends AbstractBeanDefinitionParser {
    @Override
    protected final AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        Class<?> beanClass;
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        String parentName = this.getParentName(element);
        if (parentName != null) {
            builder.getRawBeanDefinition().setParentName(parentName);
        }
        if ((beanClass = this.getBeanClass(element)) != null) {
            builder.getRawBeanDefinition().setBeanClass(beanClass);
        } else {
            String beanClassName = this.getBeanClassName(element);
            if (beanClassName != null) {
                builder.getRawBeanDefinition().setBeanClassName(beanClassName);
            }
        }
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
        BeanDefinition containingBd = parserContext.getContainingBeanDefinition();
        if (containingBd != null) {
            builder.setScope(containingBd.getScope());
        }
        if (parserContext.isDefaultLazyInit()) {
            builder.setLazyInit(true);
        }
        this.doParse(element, parserContext, builder);
        return builder.getBeanDefinition();
    }

    @Nullable
    protected String getParentName(Element element) {
        return null;
    }

    @Nullable
    protected Class<?> getBeanClass(Element element) {
        return null;
    }

    @Nullable
    protected String getBeanClassName(Element element) {
        return null;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        this.doParse(element, builder);
    }

    protected void doParse(Element element, BeanDefinitionBuilder builder) {
    }
}

