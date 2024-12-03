/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
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

    @Override
    public Object postProcessBeforeInitialization(Object bean2, String beanName) throws BeansException {
        if (this.getServletContext() != null && bean2 instanceof ServletContextAware) {
            ((ServletContextAware)bean2).setServletContext(this.getServletContext());
        }
        if (this.getServletConfig() != null && bean2 instanceof ServletConfigAware) {
            ((ServletConfigAware)bean2).setServletConfig(this.getServletConfig());
        }
        return bean2;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean2, String beanName) {
        return bean2;
    }
}

