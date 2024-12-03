/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  javax.servlet.ServletContext
 *  org.apache.log4j.Logger
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.web.context.ContextLoader
 *  org.springframework.web.context.WebApplicationContext
 *  org.springframework.web.context.support.WebApplicationContextUtils
 */
package com.atlassian.spring.container;

import com.atlassian.event.Event;
import com.atlassian.spring.container.AtlassianBeanFactory;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerContextLoadedEvent;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringContainerContext
implements ContainerContext {
    private static final Logger log = Logger.getLogger(SpringContainerContext.class);
    private volatile ServletContext servletContext;
    private volatile ApplicationContext applicationContext;
    private volatile AtlassianBeanFactory beanFactory = new AtlassianBeanFactory(null);

    public void setServletContext(ServletContext context) {
        this.servletContext = context;
        this.setApplicationContext((ApplicationContext)WebApplicationContextUtils.getWebApplicationContext((ServletContext)context));
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public Object getComponent(Object key) throws ComponentNotFoundException {
        if (this.applicationContext == null) {
            log.fatal((Object)"Spring Application context has not been set");
            throw new IllegalStateException("Spring Application context has not been set");
        }
        if (key == null) {
            log.error((Object)"The component key cannot be null");
            throw new ComponentNotFoundException("The component key cannot be null");
        }
        if (key instanceof Class) {
            String[] names = this.beanFactory.getBeanNamesForType((Class)key);
            if (names == null || names.length == 0 || names.length > 1) {
                throw new ComponentNotFoundException("The container is unable to resolve single instance of " + ((Class)key).getName() + " number of instances found was: " + names.length);
            }
            key = names[0];
        }
        try {
            return this.beanFactory.getBean(key.toString());
        }
        catch (BeansException e) {
            throw new ComponentNotFoundException("Failed to find component: " + e.getMessage(), e);
        }
    }

    @Override
    public Object createComponent(Class clazz) {
        return this.beanFactory.autowire(clazz, 1, false);
    }

    @Override
    public Object createCompleteComponent(Class clazz) {
        return this.beanFactory.createBean(clazz, 1, false);
    }

    @Override
    public void autowireComponent(Object bean) {
        if (this.beanFactory != null) {
            this.beanFactory.autowireBeanProperties(bean, 1, false);
        } else {
            log.debug((Object)("ApplicationContext is null or has not been set. Cannot proceed with autowiring of component: " + bean));
        }
    }

    public void setApplicationContext(ApplicationContext appContext) throws ApplicationContextException {
        if (appContext != null) {
            this.applicationContext = appContext;
            this.beanFactory = new AtlassianBeanFactory(appContext.getAutowireCapableBeanFactory());
        } else {
            this.applicationContext = null;
            this.beanFactory = null;
        }
    }

    @Override
    public synchronized void refresh() {
        ContextLoader loader = new ContextLoader();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext((ServletContext)this.servletContext);
        if (ctx != null) {
            loader.closeWebApplicationContext(this.servletContext);
        }
        loader.initWebApplicationContext(this.servletContext);
        if (this.applicationContext == null) {
            this.setApplicationContext((ApplicationContext)WebApplicationContextUtils.getWebApplicationContext((ServletContext)this.servletContext));
        }
        this.contextReloaded();
    }

    @Override
    public boolean isSetup() {
        return this.applicationContext != null;
    }

    protected void contextReloaded() {
        if (this.applicationContext != null) {
            this.applicationContext.publishEvent((ApplicationEvent)new ContainerContextLoadedEvent(this.applicationContext));
        }
    }

    protected ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void publishEvent(Event e) {
        this.applicationContext.publishEvent((ApplicationEvent)e);
    }
}

