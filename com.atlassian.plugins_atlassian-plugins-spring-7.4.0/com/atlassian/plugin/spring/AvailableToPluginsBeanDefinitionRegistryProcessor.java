/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
 *  org.springframework.core.type.MethodMetadata
 */
package com.atlassian.plugin.spring;

import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import com.atlassian.plugin.spring.AvailableToPlugins;
import com.atlassian.plugin.spring.PluginBeanDefinitionRegistry;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.type.MethodMetadata;

public class AvailableToPluginsBeanDefinitionRegistryProcessor
implements BeanDefinitionRegistryPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(AvailableToPluginsBeanDefinitionRegistryProcessor.class);
    private final Function<BeanDefinitionRegistry, PluginBeanDefinitionRegistry> registryFactory;

    public AvailableToPluginsBeanDefinitionRegistryProcessor() {
        this(PluginBeanDefinitionRegistry::new);
    }

    @VisibleForTesting
    AvailableToPluginsBeanDefinitionRegistryProcessor(Function<BeanDefinitionRegistry, PluginBeanDefinitionRegistry> registryFactory) {
        this.registryFactory = registryFactory;
    }

    public void postProcessBeanDefinitionRegistry(@Nonnull BeanDefinitionRegistry registry) {
        log.debug("Scanning al;l bean definitions for plugin-available @Bean methods");
        PluginBeanDefinitionRegistry pluginBeanDefinitionRegistry = this.registryFactory.apply(registry);
        for (String beanName : registry.getBeanDefinitionNames()) {
            boolean isPluginAvailable;
            MethodMetadata factoryMethodMetadata;
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (!(beanDefinition instanceof AnnotatedBeanDefinition) || (factoryMethodMetadata = ((AnnotatedBeanDefinition)beanDefinition).getFactoryMethodMetadata()) == null || !(isPluginAvailable = factoryMethodMetadata.isAnnotated(AvailableToPlugins.class.getName()))) continue;
            if (beanDefinition.isSingleton()) {
                log.debug("Registering bean '{}' as plugin-available", (Object)beanName);
                this.registerPluginAvailableBean(beanName, factoryMethodMetadata, pluginBeanDefinitionRegistry);
                continue;
            }
            log.warn("Bean '{}' is not singleton-scoped, and cannot be made available to plugins", (Object)beanName);
        }
    }

    private void registerPluginAvailableBean(String beanName, MethodMetadata methodMetadata, PluginBeanDefinitionRegistry registry) {
        String[] interfacesAttribute;
        registry.addBeanName(beanName);
        Map annotationAttributes = methodMetadata.getAnnotationAttributes(AvailableToPlugins.class.getName(), true);
        String valueAttribute = (String)annotationAttributes.get("value");
        if (!valueAttribute.equals(Void.class.getName())) {
            registry.addBeanInterface(beanName, valueAttribute);
        }
        for (String interfaceName : interfacesAttribute = (String[])annotationAttributes.get("interfaces")) {
            registry.addBeanInterface(beanName, interfaceName);
        }
        registry.addContextClassLoaderStrategy(beanName, (ContextClassLoaderStrategy)annotationAttributes.get("contextClassLoaderStrategy"));
        boolean trackBundle = (Boolean)annotationAttributes.get("trackBundle");
        if (trackBundle) {
            registry.addBundleTrackingBean(beanName);
        }
    }

    public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory beanFactory) {
    }
}

