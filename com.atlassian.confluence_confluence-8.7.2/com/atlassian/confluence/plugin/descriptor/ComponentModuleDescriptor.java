/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.SpringContainerContext
 *  javax.servlet.ServletContext
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.BeanCreationException
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.web.context.support.WebApplicationContextUtils
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.setup.ConfluenceListableBeanFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.SpringContainerContext;
import javax.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ComponentModuleDescriptor
extends AbstractModuleDescriptor
implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ComponentModuleDescriptor.class);
    String alias;
    private ApplicationContext appContext;

    public ComponentModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.alias = element.attributeValue("alias");
        if (!StringUtils.isNotEmpty((CharSequence)this.alias)) {
            this.alias = this.getKey();
        }
    }

    public Object getModule() {
        throw new UnsupportedOperationException("You cannot retrieve a component instance - Spring-ified");
    }

    public void enabled() {
        super.enabled();
        ConfluenceListableBeanFactory beanFactory = this.getGlobalBeanFactoryUsingHacks();
        if (beanFactory != null) {
            if (beanFactory.containsBean(this.alias)) {
                throw new IllegalStateException("Can not overwrite an existing bean definition: " + this.alias);
            }
            log.debug("Creating bean definition for " + this.alias + " with class " + this.getModuleClass().getName());
            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(this.getModuleClass(), 1, false);
            log.debug("Registering bean definition for " + this.alias + " with class " + this.getModuleClass().getName());
            beanFactory.registerBeanDefinition(this.alias, (BeanDefinition)rootBeanDefinition);
            if (StateAware.class.isAssignableFrom(this.getModuleClass())) {
                Object o = beanFactory.getBean(this.alias);
                StateAware sa = (StateAware)o;
                sa.enabled();
            }
        }
    }

    public void disabled() {
        ConfluenceListableBeanFactory beanFactory;
        if (log.isDebugEnabled()) {
            log.debug("Disabling component module " + this.getKey());
        }
        if ((beanFactory = this.getGlobalBeanFactoryUsingHacks()) != null) {
            Object o = null;
            try {
                o = beanFactory.getBean(this.alias);
            }
            catch (BeanCreationException | NoSuchBeanDefinitionException throwable) {
                // empty catch block
            }
            if (o != null && o instanceof StateAware) {
                StateAware sa = (StateAware)o;
                sa.disabled();
            }
            log.debug("Removing bean definition for " + this.alias);
            beanFactory.unregisterBeanDefinition(this.alias);
        }
        super.disabled();
    }

    private ConfluenceListableBeanFactory getGlobalBeanFactoryUsingHacks() {
        ApplicationContext applicationContext = this.getApplicationContextWithHack();
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext)applicationContext;
            if (configurableApplicationContext.getBeanFactory() instanceof ConfluenceListableBeanFactory) {
                return (ConfluenceListableBeanFactory)configurableApplicationContext.getBeanFactory();
            }
            log.error("Failed to lookup global bean factory - BeanFactory was not a ConfluenceListableBeanFactory?");
        } else {
            log.error("Failed to lookup global bean factory - ApplicationContext was not a ConfigurableApplicationContext?");
        }
        return null;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.appContext = applicationContext;
    }

    private ApplicationContext getApplicationContextWithHack() {
        SpringContainerContext springContainerContext;
        ServletContext servletContext;
        if (this.appContext != null) {
            return this.appContext;
        }
        log.warn("Using hacks to get application context");
        ContainerContext containerContext = ContainerManager.getInstance().getContainerContext();
        if (containerContext instanceof SpringContainerContext && (servletContext = (springContainerContext = (SpringContainerContext)containerContext).getServletContext()) != null) {
            return WebApplicationContextUtils.getWebApplicationContext((ServletContext)servletContext);
        }
        return null;
    }
}

