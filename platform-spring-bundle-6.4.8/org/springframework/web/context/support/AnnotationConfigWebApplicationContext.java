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
    private final Set<Class<?>> componentClasses = new LinkedHashSet();
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
    public void register(Class<?> ... componentClasses) {
        Assert.notEmpty((Object[])componentClasses, "At least one component class must be specified");
        Collections.addAll(this.componentClasses, componentClasses);
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
        if (!this.componentClasses.isEmpty()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Registering component classes: [" + StringUtils.collectionToCommaDelimitedString(this.componentClasses) + "]"));
            }
            reader.register(ClassUtils.toClassArray(this.componentClasses));
        }
        if (!this.basePackages.isEmpty()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Scanning base packages: [" + StringUtils.collectionToCommaDelimitedString(this.basePackages) + "]"));
            }
            scanner.scan(StringUtils.toStringArray(this.basePackages));
        }
        if ((configLocations = this.getConfigLocations()) != null) {
            for (String configLocation : configLocations) {
                try {
                    Class<?> clazz = ClassUtils.forName(configLocation, this.getClassLoader());
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Registering [" + configLocation + "]"));
                    }
                    reader.register(clazz);
                }
                catch (ClassNotFoundException ex) {
                    int count;
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Could not load class for config location [" + configLocation + "] - trying package scan. " + ex));
                    }
                    if ((count = scanner.scan(configLocation)) != 0 || !this.logger.isDebugEnabled()) continue;
                    this.logger.debug((Object)("No component classes found for specified class/package [" + configLocation + "]"));
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

