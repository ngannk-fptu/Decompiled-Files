/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanInstantiationException
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.Aware
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.annotation;

import java.lang.reflect.Constructor;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
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
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

abstract class ParserStrategyUtils {
    ParserStrategyUtils() {
    }

    static <T> T instantiateClass(Class<?> clazz, Class<T> assignableTo, Environment environment2, ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {
        Assert.notNull(clazz, (String)"Class must not be null");
        Assert.isAssignable(assignableTo, clazz);
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        ClassLoader classLoader = registry instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)registry).getBeanClassLoader() : resourceLoader.getClassLoader();
        Object instance = ParserStrategyUtils.createInstance(clazz, environment2, resourceLoader, registry, classLoader);
        ParserStrategyUtils.invokeAwareMethods(instance, environment2, resourceLoader, registry, classLoader);
        return (T)instance;
    }

    private static Object createInstance(Class<?> clazz, Environment environment2, ResourceLoader resourceLoader, BeanDefinitionRegistry registry, @Nullable ClassLoader classLoader) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 1 && constructors[0].getParameterCount() > 0) {
            try {
                Constructor<?> constructor = constructors[0];
                Object[] args = ParserStrategyUtils.resolveArgs(constructor.getParameterTypes(), environment2, resourceLoader, registry, classLoader);
                return BeanUtils.instantiateClass(constructor, (Object[])args);
            }
            catch (Exception ex) {
                throw new BeanInstantiationException(clazz, "No suitable constructor found", (Throwable)ex);
            }
        }
        return BeanUtils.instantiateClass(clazz);
    }

    private static Object[] resolveArgs(Class<?>[] parameterTypes, Environment environment2, ResourceLoader resourceLoader, BeanDefinitionRegistry registry, @Nullable ClassLoader classLoader) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            parameters[i] = ParserStrategyUtils.resolveParameter(parameterTypes[i], environment2, resourceLoader, registry, classLoader);
        }
        return parameters;
    }

    @Nullable
    private static Object resolveParameter(Class<?> parameterType, Environment environment2, ResourceLoader resourceLoader, BeanDefinitionRegistry registry, @Nullable ClassLoader classLoader) {
        if (parameterType == Environment.class) {
            return environment2;
        }
        if (parameterType == ResourceLoader.class) {
            return resourceLoader;
        }
        if (parameterType == BeanFactory.class) {
            return registry instanceof BeanFactory ? registry : null;
        }
        if (parameterType == ClassLoader.class) {
            return classLoader;
        }
        throw new IllegalStateException("Illegal method parameter type: " + parameterType.getName());
    }

    private static void invokeAwareMethods(Object parserStrategyBean, Environment environment2, ResourceLoader resourceLoader, BeanDefinitionRegistry registry, @Nullable ClassLoader classLoader) {
        if (parserStrategyBean instanceof Aware) {
            if (parserStrategyBean instanceof BeanClassLoaderAware && classLoader != null) {
                ((BeanClassLoaderAware)parserStrategyBean).setBeanClassLoader(classLoader);
            }
            if (parserStrategyBean instanceof BeanFactoryAware && registry instanceof BeanFactory) {
                ((BeanFactoryAware)parserStrategyBean).setBeanFactory((BeanFactory)registry);
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

