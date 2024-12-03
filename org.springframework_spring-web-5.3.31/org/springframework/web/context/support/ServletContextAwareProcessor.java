/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

public class ServletContextAwareProcessor
implements BeanPostProcessor {
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private ServletConfig servletConfig;

    protected ServletContextAwareProcessor() {
    }

    public ServletContextAwareProcessor(ServletContext servletContext) {
        this(servletContext, null);
    }

    public ServletContextAwareProcessor(ServletConfig servletConfig) {
        this(null, servletConfig);
    }

    public ServletContextAwareProcessor(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        this.servletContext = servletContext;
        this.servletConfig = servletConfig;
    }

    @Nullable
    protected ServletContext getServletContext() {
        if (this.servletContext == null && this.getServletConfig() != null) {
            return this.getServletConfig().getServletContext();
        }
        return this.servletContext;
    }

    @Nullable
    protected ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (this.getServletContext() != null && bean instanceof ServletContextAware) {
            ((ServletContextAware)bean).setServletContext(this.getServletContext());
        }
        if (this.getServletConfig() != null && bean instanceof ServletConfigAware) {
            ((ServletConfigAware)bean).setServletConfig(this.getServletConfig());
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}

