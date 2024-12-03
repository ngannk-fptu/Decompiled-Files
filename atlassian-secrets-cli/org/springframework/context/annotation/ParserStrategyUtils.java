/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

abstract class ParserStrategyUtils {
    ParserStrategyUtils() {
    }

    public static void invokeAwareMethods(Object parserStrategyBean, Environment environment2, ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {
        if (parserStrategyBean instanceof Aware) {
            if (parserStrategyBean instanceof BeanClassLoaderAware) {
                ClassLoader classLoader;
                ClassLoader classLoader2 = classLoader = registry instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)((Object)registry)).getBeanClassLoader() : resourceLoader.getClassLoader();
                if (classLoader != null) {
                    ((BeanClassLoaderAware)parserStrategyBean).setBeanClassLoader(classLoader);
                }
            }
            if (parserStrategyBean instanceof BeanFactoryAware && registry instanceof BeanFactory) {
                ((BeanFactoryAware)parserStrategyBean).setBeanFactory((BeanFactory)((Object)registry));
            }
            if (parserStrategyBean instanceof EnvironmentAware) {
                ((EnvironmentAware)parserStrategyBean).setEnvironment(environment2);
            }
            if (parserStrategyBean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware)parserStrategyBean).setResourceLoader(resourceLoader);
            }
        }
    }
}

