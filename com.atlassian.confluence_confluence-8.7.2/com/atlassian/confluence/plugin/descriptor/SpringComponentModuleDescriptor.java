/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.SpringContainerContext
 *  javax.servlet.ServletContext
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Attribute
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.DocumentFactory
 *  org.dom4j.Element
 *  org.dom4j.io.DOMWriter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.web.context.support.WebApplicationContextUtils
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.setup.ConfluenceListableBeanFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.SpringContainerContext;
import javax.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringComponentModuleDescriptor
extends AbstractModuleDescriptor
implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringComponentModuleDescriptor.class);
    private String alias;
    private Document document;
    private ApplicationContext appContext;

    public SpringComponentModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.alias = element.attributeValue("alias");
        if (!StringUtils.isNotEmpty((CharSequence)this.alias)) {
            this.alias = this.getKey();
        }
        DocumentFactory factory = DocumentFactory.getInstance();
        Element beans = factory.createElement("beans");
        Element bean = factory.createElement("bean");
        beans.add(bean);
        bean.addAttribute("id", this.alias);
        bean.addAttribute("class", element.attributeValue("class"));
        for (Attribute a : element.attributes()) {
            if (a.getName().equals("key") || a.getName().equals("name") || a.getName().equals("class") || a.getName().equals("alias")) continue;
            bean.addAttribute(a.getName(), a.getValue());
        }
        for (int i = 0; i < element.nodeCount(); ++i) {
            if (!(element.node(i) instanceof Element)) continue;
            bean.add(((Element)element.node(i)).createCopy());
        }
        this.document = factory.createDocument();
        this.document.add(beans);
    }

    public Object getModule() {
        try {
            return ContainerManager.getComponent((String)this.alias);
        }
        catch (ComponentNotFoundException cnfe) {
            return null;
        }
    }

    public void enabled() {
        super.enabled();
        DefaultListableBeanFactory beanFactory = this.getGlobalBeanFactoryUsingHacks();
        if (beanFactory != null) {
            if (beanFactory.containsBean(this.alias)) {
                throw new IllegalStateException("Can not overwrite an existing bean definition: " + this.alias);
            }
            log.debug("Creating bean definition for " + this.alias + " with class " + this.getModuleClass().getName());
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
            if (this.plugin.isDynamicallyLoaded()) {
                reader.setBeanClassLoader(this.plugin.getClassLoader());
            }
            DOMWriter writer = new DOMWriter();
            try {
                reader.registerBeanDefinitions(writer.write(this.document), (Resource)new ClassPathResource("x"));
                ContainerManager.getComponent((String)this.alias);
            }
            catch (DocumentException de) {
                log.error("failed to convert Document to DOM", (Throwable)de);
            }
        }
    }

    public void disabled() {
        DefaultListableBeanFactory beanFactory = this.getGlobalBeanFactoryUsingHacks();
        if (beanFactory != null) {
            log.debug("Removing bean definition for " + this.alias);
            ((ConfluenceListableBeanFactory)beanFactory).unregisterBeanDefinition(this.alias);
        }
        super.disabled();
    }

    private DefaultListableBeanFactory getGlobalBeanFactoryUsingHacks() {
        ApplicationContext applicationContext = this.getApplicationContextWithHack();
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext)applicationContext;
            if (configurableApplicationContext.getBeanFactory() instanceof DefaultListableBeanFactory) {
                return (DefaultListableBeanFactory)configurableApplicationContext.getBeanFactory();
            }
            log.error("Failed to lookup global bean factory - BeanFactory was not a DefaultListableBeanFactory?");
        } else {
            log.error("Failed to lookup global bean factory - ApplicationContext was not a ConfigurableApplicationContext?");
        }
        return null;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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

