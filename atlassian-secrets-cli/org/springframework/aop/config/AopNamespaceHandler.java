/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.config;

import org.springframework.aop.config.AspectJAutoProxyBeanDefinitionParser;
import org.springframework.aop.config.ConfigBeanDefinitionParser;
import org.springframework.aop.config.ScopedProxyBeanDefinitionDecorator;
import org.springframework.aop.config.SpringConfiguredBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class AopNamespaceHandler
extends NamespaceHandlerSupport {
    @Override
    public void init() {
        this.registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
        this.registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
        this.registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());
        this.registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
    }
}

