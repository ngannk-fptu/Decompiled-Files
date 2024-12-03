/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.core.Ordered
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.UrlResource
 */
package com.atlassian.spring.hosted;

import com.atlassian.spring.hosted.AllowHostedOverride;
import com.atlassian.spring.hosted.HostedOverrideNotAllowedException;
import java.lang.annotation.Annotation;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class HostedBeanFactoryPostProcessor
implements BeanFactoryPostProcessor,
Ordered {
    private static final Logger log = LoggerFactory.getLogger(HostedBeanFactoryPostProcessor.class);
    private String resource = "META-INF/hosted-application-context-overrides.xml";

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof BeanDefinitionRegistry)) {
            throw new IllegalArgumentException("Bean factory must be an instance of " + BeanDefinitionRegistry.class.getName() + ", otherwise this post processor can't do its job.");
        }
        URL url = this.getHostedOverrides();
        if (url != null) {
            log.info("Overriding application context with " + url.toString());
            this.validateOverridingContext(beanFactory, url);
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
            reader.loadBeanDefinitions((Resource)new UrlResource(url));
        }
    }

    private void validateOverridingContext(ConfigurableListableBeanFactory beanFactory, URL url) throws HostedOverrideNotAllowedException {
        DefaultListableBeanFactory validatingRegistry = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader validationReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)validatingRegistry);
        validationReader.loadBeanDefinitions((Resource)new UrlResource(url));
        for (String name : validatingRegistry.getBeanDefinitionNames()) {
            if (!beanFactory.containsBeanDefinition(name)) continue;
            try {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
                if (!this.allowedToOverride(beanDefinition)) {
                    throw new HostedOverrideNotAllowedException(name, url);
                }
                if (!log.isDebugEnabled()) continue;
                log.debug("Overriding bean: " + name);
            }
            catch (NoSuchBeanDefinitionException e) {
                if (!log.isDebugEnabled()) continue;
                log.debug("Allowing non overriding bean: " + name);
            }
        }
    }

    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    private URL getHostedOverrides() {
        return this.getClass().getClassLoader().getResource(this.resource);
    }

    private boolean allowedToOverride(BeanDefinition beanDefinition) {
        if (beanDefinition.hasAttribute("override")) {
            return Boolean.parseBoolean(beanDefinition.getAttribute("override").toString());
        }
        String className = beanDefinition.getBeanClassName();
        if (className != null) {
            try {
                Class<?> clazz = Class.forName(className);
                if (this.hasAnnotation(clazz, AllowHostedOverride.class)) {
                    return true;
                }
            }
            catch (ClassNotFoundException e) {
                log.error("Could not find class for potential override", (Throwable)e);
            }
        }
        return false;
    }

    private boolean hasAnnotation(Class clazz, Class<? extends Annotation> annotation) {
        if (clazz.getAnnotation(annotation) != null) {
            return true;
        }
        for (Class<?> inter : clazz.getInterfaces()) {
            if (!this.hasAnnotation(inter, annotation)) continue;
            return true;
        }
        if (clazz.getSuperclass() != null) {
            return this.hasAnnotation(clazz.getSuperclass(), annotation);
        }
        return false;
    }
}

