/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.context.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
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
    public void init() {
        this.registerBeanDefinitionParser("property-placeholder", (BeanDefinitionParser)new PropertyPlaceholderBeanDefinitionParser());
        this.registerBeanDefinitionParser("property-override", (BeanDefinitionParser)new PropertyOverrideBeanDefinitionParser());
        this.registerBeanDefinitionParser("annotation-config", new AnnotationConfigBeanDefinitionParser());
        this.registerBeanDefinitionParser("component-scan", new ComponentScanBeanDefinitionParser());
        this.registerBeanDefinitionParser("load-time-weaver", (BeanDefinitionParser)new LoadTimeWeaverBeanDefinitionParser());
        this.registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
        this.registerBeanDefinitionParser("mbean-export", (BeanDefinitionParser)new MBeanExportBeanDefinitionParser());
        this.registerBeanDefinitionParser("mbean-server", (BeanDefinitionParser)new MBeanServerBeanDefinitionParser());
    }
}

