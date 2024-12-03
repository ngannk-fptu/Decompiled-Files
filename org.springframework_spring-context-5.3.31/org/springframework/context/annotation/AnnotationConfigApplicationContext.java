/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinitionCustomizer
 *  org.springframework.beans.factory.support.BeanNameGenerator
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.core.env.ConfigurableEnvironment
 *  org.springframework.core.env.Environment
 *  org.springframework.core.metrics.StartupStep
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.annotation;

import java.util.Arrays;
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
import org.springframework.core.env.Environment;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AnnotationConfigApplicationContext
extends GenericApplicationContext
implements AnnotationConfigRegistry {
    private final AnnotatedBeanDefinitionReader reader;
    private final ClassPathBeanDefinitionScanner scanner;

    public AnnotationConfigApplicationContext() {
        StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
        this.reader = new AnnotatedBeanDefinitionReader(this);
        createAnnotatedBeanDefReader.end();
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }

    public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
        this.reader = new AnnotatedBeanDefinitionReader(this);
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }

    public AnnotationConfigApplicationContext(Class<?> ... componentClasses) {
        this();
        this.register(componentClasses);
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
        this.reader.setEnvironment((Environment)environment2);
        this.scanner.setEnvironment((Environment)environment2);
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.reader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
        this.getBeanFactory().registerSingleton("org.springframework.context.annotation.internalConfigurationBeanNameGenerator", (Object)beanNameGenerator);
    }

    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.reader.setScopeMetadataResolver(scopeMetadataResolver);
        this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
    }

    @Override
    public void register(Class<?> ... componentClasses) {
        Assert.notEmpty((Object[])componentClasses, (String)"At least one component class must be specified");
        StartupStep registerComponentClass = this.getApplicationStartup().start("spring.context.component-classes.register").tag("classes", () -> Arrays.toString(componentClasses));
        this.reader.register(componentClasses);
        registerComponentClass.end();
    }

    @Override
    public void scan(String ... basePackages) {
        Assert.notEmpty((Object[])basePackages, (String)"At least one base package must be specified");
        StartupStep scanPackages = this.getApplicationStartup().start("spring.context.base-packages.scan").tag("packages", () -> Arrays.toString(basePackages));
        this.scanner.scan(basePackages);
        scanPackages.end();
    }

    @Override
    public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, @Nullable Supplier<T> supplier, BeanDefinitionCustomizer ... customizers) {
        this.reader.registerBean(beanClass, beanName, supplier, customizers);
    }
}

