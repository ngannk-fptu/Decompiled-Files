/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.xml.ResourceEntityResolver
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.support;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.xml.sax.EntityResolver;

public abstract class AbstractXmlApplicationContext
extends AbstractRefreshableConfigApplicationContext {
    private boolean validating = true;

    public AbstractXmlApplicationContext() {
    }

    public AbstractXmlApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
        beanDefinitionReader.setEnvironment((Environment)this.getEnvironment());
        beanDefinitionReader.setResourceLoader((ResourceLoader)this);
        beanDefinitionReader.setEntityResolver((EntityResolver)new ResourceEntityResolver((ResourceLoader)this));
        this.initBeanDefinitionReader(beanDefinitionReader);
        this.loadBeanDefinitions(beanDefinitionReader);
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
        reader.setValidating(this.validating);
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        String[] configLocations;
        Resource[] configResources = this.getConfigResources();
        if (configResources != null) {
            reader.loadBeanDefinitions(configResources);
        }
        if ((configLocations = this.getConfigLocations()) != null) {
            reader.loadBeanDefinitions(configLocations);
        }
    }

    @Nullable
    protected Resource[] getConfigResources() {
        return null;
    }
}

