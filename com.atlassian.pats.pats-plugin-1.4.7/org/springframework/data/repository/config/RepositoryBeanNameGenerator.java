/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConstructorArgumentValues$ValueHolder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.BeanNameGenerator
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.config;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

class RepositoryBeanNameGenerator {
    private final ClassLoader beanClassLoader;
    private final BeanNameGenerator generator;
    private final BeanDefinitionRegistry registry;

    public RepositoryBeanNameGenerator(ClassLoader beanClassLoader, BeanNameGenerator generator, BeanDefinitionRegistry registry) {
        Assert.notNull((Object)beanClassLoader, (String)"Bean ClassLoader must not be null!");
        Assert.notNull((Object)generator, (String)"BeanNameGenerator must not be null!");
        Assert.notNull((Object)registry, (String)"BeanDefinitionRegistry must not be null!");
        this.beanClassLoader = beanClassLoader;
        this.generator = generator;
        this.registry = registry;
    }

    public String generateBeanName(BeanDefinition definition) {
        AnnotatedBeanDefinition beanDefinition = definition instanceof AnnotatedBeanDefinition ? (AnnotatedBeanDefinition)definition : new AnnotatedGenericBeanDefinition(this.getRepositoryInterfaceFrom(definition));
        return this.generator.generateBeanName((BeanDefinition)beanDefinition, this.registry);
    }

    private Class<?> getRepositoryInterfaceFrom(BeanDefinition beanDefinition) {
        ConstructorArgumentValues.ValueHolder argumentValue = beanDefinition.getConstructorArgumentValues().getArgumentValue(0, Class.class);
        if (argumentValue == null) {
            throw new IllegalStateException(String.format("Failed to obtain first constructor parameter value of BeanDefinition %s!", beanDefinition));
        }
        Object value = argumentValue.getValue();
        if (value == null) {
            throw new IllegalStateException(String.format("Value of first constructor parameter value of BeanDefinition %s is null!", beanDefinition));
        }
        if (value instanceof Class) {
            return (Class)value;
        }
        try {
            return ClassUtils.forName((String)value.toString(), (ClassLoader)this.beanClassLoader);
        }
        catch (Exception o_O) {
            throw new RuntimeException(o_O);
        }
    }
}

