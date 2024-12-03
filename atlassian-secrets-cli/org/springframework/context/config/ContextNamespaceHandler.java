/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.annotation.AnnotationConfigBeanDefinitionParser;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.springframework.context.config.LoadTimeWeaverBeanDefinitionParser;
import org.springframework.context.config.MBeanExportBeanDefinitionParser;
import org.springframework.context.config.MBeanServerBeanDefinitionParser;
import org.springframework.context.config.PropertyOverrideBeanDefinitionParser;
import org.springframework.context.config.PropertyPlaceholderBeanDefinitionParser;
import org.springframework.context.config.SpringConfiguredBeanDefinitionParser;

public class ContextNamespaceHandler
extends NamespaceHandlerSupport {
    @Override
    public void init() {
        this.registerBeanDefinitionParser("property-placeholder", new PropertyPlaceholderBeanDefinitionParser());
        this.registerBeanDefinitionParser("property-override", new PropertyOverrideBeanDefinitionParser());
        this.registerBeanDefinitionParser("annotation-config", new AnnotationConfigBeanDefinitionParser());
        this.registerBeanDefinitionParser("component-scan", new ComponentScanBeanDefinitionParser());
        this.registerBeanDefinitionParser("load-time-weaver", new LoadTimeWeaverBeanDefinitionParser());
        this.registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
        this.registerBeanDefinitionParser("mbean-export", new MBeanExportBeanDefinitionParser());
        this.registerBeanDefinitionParser("mbean-server", new MBeanServerBeanDefinitionParser());
    }
}

