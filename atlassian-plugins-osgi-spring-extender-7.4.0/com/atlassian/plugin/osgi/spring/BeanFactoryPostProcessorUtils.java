/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.RootBeanDefinition
 */
package com.atlassian.plugin.osgi.spring;

import java.util.function.Supplier;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

final class BeanFactoryPostProcessorUtils {
    private BeanFactoryPostProcessorUtils() {
    }

    static <T extends BeanPostProcessor> void registerPostProcessor(ConfigurableListableBeanFactory beanFactory, String beanName, Class<T> beanClass, Supplier<T> postProcessorSupplier) {
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
            if (!registry.containsBeanDefinition(beanName)) {
                RootBeanDefinition def = new RootBeanDefinition(beanClass);
                def.setRole(2);
                registry.registerBeanDefinition(beanName, (BeanDefinition)def);
            }
        } else {
            BeanPostProcessor postProcessor = (BeanPostProcessor)postProcessorSupplier.get();
            if (postProcessor instanceof BeanFactoryAware) {
                ((BeanFactoryAware)postProcessor).setBeanFactory((BeanFactory)beanFactory);
            }
            beanFactory.addBeanPostProcessor(postProcessor);
        }
    }
}

