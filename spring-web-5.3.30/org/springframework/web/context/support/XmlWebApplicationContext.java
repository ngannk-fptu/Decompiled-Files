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
 *  org.springframework.core.io.ResourceLoader
 */
package org.springframework.web.context.support;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.xml.sax.EntityResolver;

public class XmlWebApplicationContext
extends AbstractRefreshableWebApplicationContext {
    public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";
    public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
    public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";

    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
        beanDefinitionReader.setEnvironment((Environment)this.getEnvironment());
        beanDefinitionReader.setResourceLoader((ResourceLoader)this);
        beanDefinitionReader.setEntityResolver((EntityResolver)new ResourceEntityResolver((ResourceLoader)this));
        this.initBeanDefinitionReader(beanDefinitionReader);
        this.loadBeanDefinitions(beanDefinitionReader);
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws IOException {
        String[] configLocations = this.getConfigLocations();
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                reader.loadBeanDefinitions(configLocation);
            }
        }
    }

    protected String[] getDefaultConfigLocations() {
        if (this.getNamespace() != null) {
            return new String[]{DEFAULT_CONFIG_LOCATION_PREFIX + this.getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX};
        }
        return new String[]{DEFAULT_CONFIG_LOCATION};
    }
}

