/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.AnnotationConfigApplicationContext
 *  org.springframework.web.context.ServletConfigAware
 *  org.springframework.web.context.ServletContextAware
 *  org.springframework.web.context.support.ServletContextAwareProcessor
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.setup.ConfluenceListableBeanFactory;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextAwareProcessor;

public class ConfluenceAnnotationConfigApplicationContext
extends AnnotationConfigApplicationContext {
    private final ServletContext servletContext;

    public ConfluenceAnnotationConfigApplicationContext(ServletContext servletContext) {
        super((DefaultListableBeanFactory)new ConfluenceListableBeanFactory());
        this.servletContext = servletContext;
    }

    public ConfluenceAnnotationConfigApplicationContext(ApplicationContext parent, ServletContext servletContext) {
        super((DefaultListableBeanFactory)new ConfluenceListableBeanFactory());
        super.setParent(parent);
        this.servletContext = servletContext;
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.postProcessBeanFactory(beanFactory);
        if (this.servletContext != null) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor)new ServletContextAwareProcessor(this.servletContext));
            beanFactory.ignoreDependencyInterface(ServletContextAware.class);
            beanFactory.ignoreDependencyInterface(ServletConfigAware.class);
        }
    }
}

