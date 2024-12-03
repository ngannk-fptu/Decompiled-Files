/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.xml.ResourceEntityResolver
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.core.Ordered
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.web.context.support.GenericWebApplicationContext
 */
package org.springframework.web.servlet.view;

import java.util.Locale;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.xml.sax.EntityResolver;

@Deprecated
public class XmlViewResolver
extends AbstractCachingViewResolver
implements Ordered,
InitializingBean,
DisposableBean {
    public static final String DEFAULT_LOCATION = "/WEB-INF/views.xml";
    @Nullable
    private Resource location;
    @Nullable
    private ConfigurableApplicationContext cachedFactory;
    private int order = Integer.MAX_VALUE;

    public void setLocation(Resource location) {
        this.location = location;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void afterPropertiesSet() throws BeansException {
        if (this.isCache()) {
            this.initFactory();
        }
    }

    @Override
    protected Object getCacheKey(String viewName, Locale locale) {
        return viewName;
    }

    @Override
    protected View loadView(String viewName, Locale locale) throws BeansException {
        BeanFactory factory = this.initFactory();
        try {
            return (View)factory.getBean(viewName, View.class);
        }
        catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    protected synchronized BeanFactory initFactory() throws BeansException {
        if (this.cachedFactory != null) {
            return this.cachedFactory;
        }
        ApplicationContext applicationContext = this.obtainApplicationContext();
        Resource actualLocation = this.location;
        if (actualLocation == null) {
            actualLocation = applicationContext.getResource(DEFAULT_LOCATION);
        }
        GenericWebApplicationContext factory = new GenericWebApplicationContext();
        factory.setParent(applicationContext);
        factory.setServletContext(this.getServletContext());
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)factory);
        reader.setEnvironment(applicationContext.getEnvironment());
        reader.setEntityResolver((EntityResolver)new ResourceEntityResolver((ResourceLoader)applicationContext));
        reader.loadBeanDefinitions(actualLocation);
        factory.refresh();
        if (this.isCache()) {
            this.cachedFactory = factory;
        }
        return factory;
    }

    public void destroy() throws BeansException {
        if (this.cachedFactory != null) {
            this.cachedFactory.close();
        }
    }
}

