/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.web.servlet.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.web.servlet.config.AnnotationDrivenBeanDefinitionParser;
import org.springframework.web.servlet.config.CorsBeanDefinitionParser;
import org.springframework.web.servlet.config.DefaultServletHandlerBeanDefinitionParser;
import org.springframework.web.servlet.config.FreeMarkerConfigurerBeanDefinitionParser;
import org.springframework.web.servlet.config.GroovyMarkupConfigurerBeanDefinitionParser;
import org.springframework.web.servlet.config.InterceptorsBeanDefinitionParser;
import org.springframework.web.servlet.config.ResourcesBeanDefinitionParser;
import org.springframework.web.servlet.config.ScriptTemplateConfigurerBeanDefinitionParser;
import org.springframework.web.servlet.config.TilesConfigurerBeanDefinitionParser;
import org.springframework.web.servlet.config.ViewControllerBeanDefinitionParser;
import org.springframework.web.servlet.config.ViewResolversBeanDefinitionParser;

public class MvcNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        this.registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
        this.registerBeanDefinitionParser("default-servlet-handler", new DefaultServletHandlerBeanDefinitionParser());
        this.registerBeanDefinitionParser("interceptors", new InterceptorsBeanDefinitionParser());
        this.registerBeanDefinitionParser("resources", new ResourcesBeanDefinitionParser());
        this.registerBeanDefinitionParser("view-controller", new ViewControllerBeanDefinitionParser());
        this.registerBeanDefinitionParser("redirect-view-controller", new ViewControllerBeanDefinitionParser());
        this.registerBeanDefinitionParser("status-controller", new ViewControllerBeanDefinitionParser());
        this.registerBeanDefinitionParser("view-resolvers", new ViewResolversBeanDefinitionParser());
        this.registerBeanDefinitionParser("tiles-configurer", (BeanDefinitionParser)new TilesConfigurerBeanDefinitionParser());
        this.registerBeanDefinitionParser("freemarker-configurer", (BeanDefinitionParser)new FreeMarkerConfigurerBeanDefinitionParser());
        this.registerBeanDefinitionParser("groovy-configurer", (BeanDefinitionParser)new GroovyMarkupConfigurerBeanDefinitionParser());
        this.registerBeanDefinitionParser("script-template-configurer", (BeanDefinitionParser)new ScriptTemplateConfigurerBeanDefinitionParser());
        this.registerBeanDefinitionParser("cors", new CorsBeanDefinitionParser());
    }
}

