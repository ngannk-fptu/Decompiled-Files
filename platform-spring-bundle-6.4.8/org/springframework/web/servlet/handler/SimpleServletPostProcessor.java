/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 */
package org.springframework.web.servlet.handler;

import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

public class SimpleServletPostProcessor
implements DestructionAwareBeanPostProcessor,
ServletContextAware,
ServletConfigAware {
    private boolean useSharedServletConfig = true;
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private ServletConfig servletConfig;

    public void setUseSharedServletConfig(boolean useSharedServletConfig) {
        this.useSharedServletConfig = useSharedServletConfig;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean2, String beanName) throws BeansException {
        return bean2;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean2, String beanName) throws BeansException {
        if (bean2 instanceof Servlet) {
            ServletConfig config = this.servletConfig;
            if (config == null || !this.useSharedServletConfig) {
                config = new DelegatingServletConfig(beanName, this.servletContext);
            }
            try {
                ((Servlet)bean2).init(config);
            }
            catch (ServletException ex) {
                throw new BeanInitializationException("Servlet.init threw exception", ex);
            }
        }
        return bean2;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean2, String beanName) throws BeansException {
        if (bean2 instanceof Servlet) {
            ((Servlet)bean2).destroy();
        }
    }

    @Override
    public boolean requiresDestruction(Object bean2) {
        return bean2 instanceof Servlet;
    }

    private static class DelegatingServletConfig
    implements ServletConfig {
        private final String servletName;
        @Nullable
        private final ServletContext servletContext;

        public DelegatingServletConfig(String servletName, @Nullable ServletContext servletContext) {
            this.servletName = servletName;
            this.servletContext = servletContext;
        }

        public String getServletName() {
            return this.servletName;
        }

        @Nullable
        public ServletContext getServletContext() {
            return this.servletContext;
        }

        @Nullable
        public String getInitParameter(String paramName) {
            return null;
        }

        public Enumeration<String> getInitParameterNames() {
            return Collections.enumeration(Collections.emptySet());
        }
    }
}

