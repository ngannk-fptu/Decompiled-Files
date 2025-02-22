/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.config;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class ScopedProxyBeanDefinitionDecorator
implements BeanDefinitionDecorator {
    private static final String PROXY_TARGET_CLASS = "proxy-target-class";

    ScopedProxyBeanDefinitionDecorator() {
    }

    @Override
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
        Element ele;
        boolean proxyTargetClass = true;
        if (node instanceof Element && (ele = (Element)node).hasAttribute(PROXY_TARGET_CLASS)) {
            proxyTargetClass = Boolean.valueOf(ele.getAttribute(PROXY_TARGET_CLASS));
        }
        BeanDefinitionHolder holder = ScopedProxyUtils.createScopedProxy(definition, parserContext.getRegistry(), proxyTargetClass);
        String targetBeanName = ScopedProxyUtils.getTargetBeanName(definition.getBeanName());
        parserContext.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(definition.getBeanDefinition(), targetBeanName));
        return holder;
    }
}

