/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.util.function.Supplier;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AnnotationConfigApplicationContext
extends GenericApplicationContext
implements AnnotationConfigRegistry {
    private final AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(this);
    private final ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this);

    public AnnotationConfigApplicationContext() {
    }

    public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    public AnnotationConfigApplicationContext(Class<?> ... annotatedClasses) {
        this();
        this.register(annotatedClasses);
        this.refresh();
    }

    public AnnotationConfigApplicationContext(String ... basePackages) {
        this();
        this.scan(basePackages);
        this.refresh();
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment2) {
        super.setEnvironment(environment2);
        this.reader.setEnvironment(environment2);
        this.scanner.setEnvironment(environment2);
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.reader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
        this.getBeanFactory().registerSingleton("org.springframework.context.annotation.internalConfigurationBeanNameGenerator", beanNameGenerator);
    }

    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.reader.setScopeMetadataResolver(scopeMetadataResolver);
        this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
    }

    @Override
    public void register(Class<?> ... annotatedClasses) {
        Assert.notEmpty((Object[])annotatedClasses, "At least one annotated class must be specified");
        this.reader.register(annotatedClasses);
    }

    @Override
    public void scan(String ... basePackages) {
        Assert.notEmpty((Object[])basePackages, "At least one base package must be specified");
        this.scanner.scan(basePackages);
    }

    public <T> void registerBean(Class<T> annotatedClass, Object ... constructorArguments) {
        this.registerBean(null, annotatedClass, constructorArguments);
    }

    public <T> void registerBean(@Nullable String beanName, Class<T> annotatedClass, Object ... constructorArguments) {
        this.reader.doRegisterBean(annotatedClass, null, beanName, null, bd -> {
            for (Object arg : constructorArguments) {
                bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
            }
        });
    }

    @Override
    public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, @Nullable Supplier<T> supplier, BeanDefinitionCustomizer ... customizers) {
        this.reader.doRegisterBean(beanClass, supplier, beanName, null, customizers);
    }
}

