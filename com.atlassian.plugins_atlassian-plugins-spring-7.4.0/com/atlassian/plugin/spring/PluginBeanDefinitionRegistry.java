/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy
 *  com.atlassian.plugin.util.Assertions
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 */
package com.atlassian.plugin.spring;

import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import com.atlassian.plugin.spring.SpringHostComponentProviderFactoryBean;
import com.atlassian.plugin.util.Assertions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class PluginBeanDefinitionRegistry {
    public static final String HOST_COMPONENT_PROVIDER = "hostComponentProvider";
    private static final String BEAN_NAMES = "beanNames";
    private static final String BEAN_INTERFACES = "beanInterfaces";
    private static final String BEAN_CONTEXT_CLASS_LOADER_STRATEGIES = "beanContextClassLoaderStrategies";
    private static final String BUNDLE_TRACKING_BEANS = "bundleTrackingBeans";
    private final BeanDefinitionRegistry registry;

    public PluginBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        this.registry = (BeanDefinitionRegistry)Assertions.notNull((String)"registry", (Object)registry);
    }

    public BeanDefinition getBeanDefinition() {
        if (!this.registry.containsBeanDefinition(HOST_COMPONENT_PROVIDER)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SpringHostComponentProviderFactoryBean.class);
            AbstractBeanDefinition beanDef = builder.getBeanDefinition();
            this.primeHostComponentBeanDefinition((BeanDefinition)beanDef);
            this.registry.registerBeanDefinition(HOST_COMPONENT_PROVIDER, (BeanDefinition)beanDef);
        }
        BeanDefinition beanDef = this.registry.getBeanDefinition(HOST_COMPONENT_PROVIDER);
        this.primeHostComponentBeanDefinition(beanDef);
        if (beanDef == null) {
            throw new IllegalStateException("Host component provider not found nor created. This should never happen.");
        }
        return beanDef;
    }

    private void primeHostComponentBeanDefinition(BeanDefinition beanDef) {
        this.ensurePropertyNotNull(beanDef, BEAN_NAMES, new ArrayList());
        this.ensurePropertyNotNull(beanDef, BEAN_INTERFACES, new HashMap());
        this.ensurePropertyNotNull(beanDef, BEAN_CONTEXT_CLASS_LOADER_STRATEGIES, new HashMap());
        this.ensurePropertyNotNull(beanDef, BUNDLE_TRACKING_BEANS, new HashSet());
    }

    private void ensurePropertyNotNull(BeanDefinition beanDef, String propertyName, Object defaultValue) {
        if (!beanDef.getPropertyValues().contains(propertyName)) {
            beanDef.getPropertyValues().addPropertyValue(propertyName, defaultValue);
        }
    }

    public void addBeanName(String beanName) {
        this.getBeanNames().add(beanName);
    }

    public void addBeanInterface(String beanName, String ifce) {
        this.addBeanInterfaces(beanName, Collections.singleton(ifce));
    }

    public void addBeanInterfaces(String beanName, Collection<String> ifces) {
        Map<String, List<String>> beanInterfaces = this.getBeanInterfaces();
        List interfaces = beanInterfaces.computeIfAbsent(beanName, k -> new ArrayList());
        interfaces.addAll(ifces);
    }

    public void addContextClassLoaderStrategy(String beanName, ContextClassLoaderStrategy strategy) {
        this.getBeanContextClassLoaderStrategies().put(beanName, strategy);
    }

    public void addBundleTrackingBean(String beanName) {
        this.getBundleTrackingBeans().add(beanName);
    }

    private Map<String, ContextClassLoaderStrategy> getBeanContextClassLoaderStrategies() {
        return (Map)this.getPropertyValue(BEAN_CONTEXT_CLASS_LOADER_STRATEGIES);
    }

    private Map<String, List<String>> getBeanInterfaces() {
        return (Map)this.getPropertyValue(BEAN_INTERFACES);
    }

    private List<String> getBeanNames() {
        return (List)this.getPropertyValue(BEAN_NAMES);
    }

    private Set<String> getBundleTrackingBeans() {
        return (Set)this.getPropertyValue(BUNDLE_TRACKING_BEANS);
    }

    private Object getPropertyValue(String propertyName) {
        return this.getBeanDefinition().getPropertyValues().getPropertyValue(propertyName).getValue();
    }
}

