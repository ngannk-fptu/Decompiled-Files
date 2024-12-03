/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.web.context.WebApplicationContext
 *  org.springframework.web.context.support.XmlWebApplicationContext
 *  org.springframework.web.servlet.DispatcherServlet
 */
package com.atlassian.plugin.web.springmvc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public final class DispatcherServlet
extends org.springframework.web.servlet.DispatcherServlet
implements ApplicationContextAware {
    private ApplicationContext pluginSpringContext;

    public DispatcherServlet() {
        this.setPublishContext(false);
    }

    protected WebApplicationContext initWebApplicationContext() {
        XmlWebApplicationContext context = new XmlWebApplicationContext(){

            protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
                beanDefinitionReader.setValidationMode(0);
                super.initBeanDefinitionReader(beanDefinitionReader);
            }
        };
        context.setId("ECWebApplicationContext");
        context.setParent(this.pluginSpringContext);
        context.setConfigLocation(this.getContextConfigLocation());
        context.setServletContext(this.getServletContext());
        context.refresh();
        this.onRefresh((ApplicationContext)context);
        return context;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        this.pluginSpringContext = applicationContext;
    }
}

