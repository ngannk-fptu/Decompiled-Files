/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.BeanNameGenerator
 *  org.springframework.context.EnvironmentAware
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.context.annotation.ConfigurationClassPostProcessor
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationUtils;
import org.springframework.util.Assert;

public abstract class RepositoryBeanDefinitionRegistrarSupport
implements ImportBeanDefinitionRegistrar,
ResourceLoaderAware,
EnvironmentAware {
    @Nonnull
    private ResourceLoader resourceLoader;
    @Nonnull
    private Environment environment;

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Deprecated
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        this.registerBeanDefinitions(metadata, registry, (BeanNameGenerator)ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR);
    }

    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator generator) {
        Assert.notNull((Object)metadata, (String)"AnnotationMetadata must not be null!");
        Assert.notNull((Object)registry, (String)"BeanDefinitionRegistry must not be null!");
        Assert.notNull((Object)this.resourceLoader, (String)"ResourceLoader must not be null!");
        if (metadata.getAnnotationAttributes(this.getAnnotation().getName()) == null) {
            return;
        }
        AnnotationRepositoryConfigurationSource configurationSource = new AnnotationRepositoryConfigurationSource(metadata, this.getAnnotation(), this.resourceLoader, this.environment, registry, generator);
        RepositoryConfigurationExtension extension = this.getExtension();
        RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);
        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, this.resourceLoader, this.environment);
        delegate.registerRepositoriesIn(registry, extension);
    }

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract RepositoryConfigurationExtension getExtension();
}

