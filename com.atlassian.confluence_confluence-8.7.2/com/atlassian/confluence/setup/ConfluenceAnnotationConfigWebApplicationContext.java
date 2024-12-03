/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.web.context.ServletConfigAware
 *  org.springframework.web.context.ServletContextAware
 *  org.springframework.web.context.support.AnnotationConfigWebApplicationContext
 *  org.springframework.web.context.support.ServletContextAwareProcessor
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.setup.ConfluenceListableBeanFactory;
import javax.servlet.ServletContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.ServletContextAwareProcessor;

public class ConfluenceAnnotationConfigWebApplicationContext
extends AnnotationConfigWebApplicationContext {
    public ConfluenceAnnotationConfigWebApplicationContext() {
    }

    public ConfluenceAnnotationConfigWebApplicationContext(ServletContext servletContext) {
        this.setServletContext(servletContext);
    }

    protected void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) {
        super.postProcessBeanFactory(beanFactory);
        if (this.getServletContext() != null) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor)new ServletContextAwareProcessor(this.getServletContext()));
            beanFactory.ignoreDependencyInterface(ServletContextAware.class);
            beanFactory.ignoreDependencyInterface(ServletConfigAware.class);
        }
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new ConfluenceListableBeanFactory(this.getInternalParentBeanFactory());
    }
}

