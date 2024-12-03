/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.context.support;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;

public class AnnotationConfigWebApplicationContext
extends AbstractRefreshableWebApplicationContext
implements AnnotationConfigRegistry {
    @Nullable
    private BeanNameGenerator beanNameGenerator;
    @Nullable
    private ScopeMetadataResolver scopeMetadataResolver;
    private final Set<Class<?>> annotatedClasses = new LinkedHashSet();
    private final Set<String> basePackages = new LinkedHashSet<String>();

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    @Nullable
    protected BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }

    public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = scopeMetadataResolver;
    }

    @Nullable
    protected ScopeMetadataResolver getScopeMetadataResolver() {
        return this.scopeMetadataResolver;
    }

    @Override
    public void register(Class<?> ... annotatedClasses) {
        Assert.notEmpty((Object[])annotatedClasses, "At least one annotated class must be specified");
        Collections.addAll(this.annotatedClasses, annotatedClasses);
    }

    @Override
    public void scan(String ... basePackages) {
        Assert.notEmpty((Object[])basePackages, "At least one base package must be specified");
        Collections.addAll(this.basePackages, basePackages);
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        String[] configLocations;
        ScopeMetadataResolver scopeMetadataResolver;
        AnnotatedBeanDefinitionReader reader = this.getAnnotatedBeanDefinitionReader(beanFactory);
        ClassPathBeanDefinitionScanner scanner = this.getClassPathBeanDefinitionScanner(beanFactory);
        BeanNameGenerator beanNameGenerator = this.getBeanNameGenerator();
        if (beanNameGenerator != null) {
            reader.setBeanNameGenerator(beanNameGenerator);
            scanner.setBeanNameGenerator(beanNameGenerator);
            beanFactory.registerSingleton("org.springframework.context.annotation.internalConfigurationBeanNameGenerator", beanNameGenerator);
        }
        if ((scopeMetadataResolver = this.getScopeMetadataResolver()) != null) {
            reader.setScopeMetadataResolver(scopeMetadataResolver);
            scanner.setScopeMetadataResolver(scopeMetadataResolver);
        }
        if (!this.annotatedClasses.isEmpty()) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Registering annotated classes: [" + StringUtils.collectionToCommaDelimitedString(this.annotatedClasses) + "]");
            }
            reader.register(ClassUtils.toClassArray(this.annotatedClasses));
        }
        if (!this.basePackages.isEmpty()) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Scanning base packages: [" + StringUtils.collectionToCommaDelimitedString(this.basePackages) + "]");
            }
            scanner.scan(StringUtils.toStringArray(this.basePackages));
        }
        if ((configLocations = this.getConfigLocations()) != null) {
            for (String configLocation : configLocations) {
                try {
                    Class<?> clazz = ClassUtils.forName(configLocation, this.getClassLoader());
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("Successfully resolved class for [" + configLocation + "]");
                    }
                    reader.register(clazz);
                }
                catch (ClassNotFoundException ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Could not load class for config location [" + configLocation + "] - trying package scan. " + ex);
                    }
                    int count = scanner.scan(configLocation);
                    if (!this.logger.isInfoEnabled()) continue;
                    if (count == 0) {
                        this.logger.info("No annotated classes found for specified class/package [" + configLocation + "]");
                        continue;
                    }
                    this.logger.info("Found " + count + " annotated classes in package [" + configLocation + "]");
                }
            }
        }
    }

    protected AnnotatedBeanDefinitionReader getAnnotatedBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        return new AnnotatedBeanDefinitionReader(beanFactory, this.getEnvironment());
    }

    protected ClassPathBeanDefinitionScanner getClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
        return new ClassPathBeanDefinitionScanner(beanFactory, true, this.getEnvironment());
    }
}

