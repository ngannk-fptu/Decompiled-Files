/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ConditionEvaluator;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AnnotatedBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;
    private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    private ConditionEvaluator conditionEvaluator;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, AnnotatedBeanDefinitionReader.getOrCreateEnvironment(registry));
    }

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment2) {
        Assert.notNull((Object)registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull((Object)environment2, "Environment must not be null");
        this.registry = registry;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment2, null);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }

    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    public void setEnvironment(Environment environment2) {
        this.conditionEvaluator = new ConditionEvaluator(this.registry, environment2, null);
    }

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator != null ? beanNameGenerator : AnnotationBeanNameGenerator.INSTANCE;
    }

    public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver();
    }

    public void register(Class<?> ... componentClasses) {
        for (Class<?> componentClass : componentClasses) {
            this.registerBean(componentClass);
        }
    }

    public void registerBean(Class<?> beanClass) {
        this.doRegisterBean(beanClass, null, null, null, null);
    }

    public void registerBean(Class<?> beanClass, @Nullable String name) {
        this.doRegisterBean(beanClass, name, null, null, null);
    }

    public void registerBean(Class<?> beanClass, Class<? extends Annotation> ... qualifiers) {
        this.doRegisterBean(beanClass, null, qualifiers, null, null);
    }

    public void registerBean(Class<?> beanClass, @Nullable String name, Class<? extends Annotation> ... qualifiers) {
        this.doRegisterBean(beanClass, name, qualifiers, null, null);
    }

    public <T> void registerBean(Class<T> beanClass, @Nullable Supplier<T> supplier) {
        this.doRegisterBean(beanClass, null, null, supplier, null);
    }

    public <T> void registerBean(Class<T> beanClass, @Nullable String name, @Nullable Supplier<T> supplier) {
        this.doRegisterBean(beanClass, name, null, supplier, null);
    }

    public <T> void registerBean(Class<T> beanClass, @Nullable String name, @Nullable Supplier<T> supplier, BeanDefinitionCustomizer ... customizers) {
        this.doRegisterBean(beanClass, name, null, supplier, customizers);
    }

    private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name, @Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier, @Nullable BeanDefinitionCustomizer[] customizers) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
        if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
            return;
        }
        abd.setInstanceSupplier(supplier);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        String beanName = name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        if (qualifiers != null) {
            for (Class<? extends Annotation> qualifier : qualifiers) {
                if (Primary.class == qualifier) {
                    abd.setPrimary(true);
                    continue;
                }
                if (Lazy.class == qualifier) {
                    abd.setLazyInit(true);
                    continue;
                }
                abd.addQualifier(new AutowireCandidateQualifier(qualifier));
            }
        }
        if (customizers != null) {
            for (BeanDefinitionCustomizer customizer2 : customizers) {
                customizer2.customize(abd);
            }
        }
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
    }

    private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
        Assert.notNull((Object)registry, "BeanDefinitionRegistry must not be null");
        if (registry instanceof EnvironmentCapable) {
            return ((EnvironmentCapable)((Object)registry)).getEnvironment();
        }
        return new StandardEnvironment();
    }
}

