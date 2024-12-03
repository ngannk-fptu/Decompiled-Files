/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.support.GenericApplicationContext
 *  org.springframework.core.env.ConfigurableEnvironment
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.lang.Nullable
 *  org.springframework.ui.context.Theme
 *  org.springframework.ui.context.ThemeSource
 *  org.springframework.ui.context.support.UiApplicationContextUtils
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.UiApplicationContextUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextAwareProcessor;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class GenericWebApplicationContext
extends GenericApplicationContext
implements ConfigurableWebApplicationContext,
ThemeSource {
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private ThemeSource themeSource;

    public GenericWebApplicationContext() {
    }

    public GenericWebApplicationContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory, ServletContext servletContext) {
        super(beanFactory);
        this.servletContext = servletContext;
    }

    @Override
    public void setServletContext(@Nullable ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    @Nullable
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public String getApplicationName() {
        return this.servletContext != null ? this.servletContext.getContextPath() : "";
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        if (this.servletContext != null) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor)new ServletContextAwareProcessor(this.servletContext));
            beanFactory.ignoreDependencyInterface(ServletContextAware.class);
        }
        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
        WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext);
    }

    protected Resource getResourceByPath(String path) {
        Assert.state((this.servletContext != null ? 1 : 0) != 0, (String)"No ServletContext available");
        return new ServletContextResource(this.servletContext, path);
    }

    protected ResourcePatternResolver getResourcePatternResolver() {
        return new ServletContextResourcePatternResolver((ResourceLoader)this);
    }

    protected void onRefresh() {
        this.themeSource = UiApplicationContextUtils.initThemeSource((ApplicationContext)this);
    }

    protected void initPropertySources() {
        ConfigurableEnvironment env = this.getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ((ConfigurableWebEnvironment)env).initPropertySources(this.servletContext, null);
        }
    }

    @Nullable
    public Theme getTheme(String themeName) {
        Assert.state((this.themeSource != null ? 1 : 0) != 0, (String)"No ThemeSource available");
        return this.themeSource.getTheme(themeName);
    }

    @Override
    public void setServletConfig(@Nullable ServletConfig servletConfig) {
    }

    @Override
    @Nullable
    public ServletConfig getServletConfig() {
        throw new UnsupportedOperationException("GenericWebApplicationContext does not support getServletConfig()");
    }

    @Override
    public void setNamespace(@Nullable String namespace) {
    }

    @Override
    @Nullable
    public String getNamespace() {
        throw new UnsupportedOperationException("GenericWebApplicationContext does not support getNamespace()");
    }

    @Override
    public void setConfigLocation(String configLocation) {
        if (StringUtils.hasText((String)configLocation)) {
            throw new UnsupportedOperationException("GenericWebApplicationContext does not support setConfigLocation(). Do you still have a 'contextConfigLocation' init-param set?");
        }
    }

    @Override
    public void setConfigLocations(String ... configLocations) {
        if (!ObjectUtils.isEmpty((Object[])configLocations)) {
            throw new UnsupportedOperationException("GenericWebApplicationContext does not support setConfigLocations(). Do you still have a 'contextConfigLocations' init-param set?");
        }
    }

    @Override
    public String[] getConfigLocations() {
        throw new UnsupportedOperationException("GenericWebApplicationContext does not support getConfigLocations()");
    }
}

