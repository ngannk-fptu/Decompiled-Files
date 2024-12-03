/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.BeanNameGenerator
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import java.util.Collections;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.RepositoryBeanNameGenerator;
import org.springframework.data.repository.config.RepositoryComponentProvider;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

public abstract class RepositoryConfigurationSourceSupport
implements RepositoryConfigurationSource {
    protected static final String DEFAULT_REPOSITORY_IMPL_POSTFIX = "Impl";
    private final Environment environment;
    private final RepositoryBeanNameGenerator beanNameGenerator;
    private final BeanDefinitionRegistry registry;

    public RepositoryConfigurationSourceSupport(Environment environment, ClassLoader classLoader, BeanDefinitionRegistry registry, BeanNameGenerator generator) {
        Assert.notNull((Object)environment, (String)"Environment must not be null!");
        Assert.notNull((Object)classLoader, (String)"ClassLoader must not be null!");
        Assert.notNull((Object)registry, (String)"BeanDefinitionRegistry must not be null!");
        this.environment = environment;
        this.beanNameGenerator = new RepositoryBeanNameGenerator(classLoader, generator, registry);
        this.registry = registry;
    }

    @Override
    public Streamable<BeanDefinition> getCandidates(ResourceLoader loader) {
        RepositoryComponentProvider scanner = new RepositoryComponentProvider(this.getIncludeFilters(), this.registry);
        scanner.setConsiderNestedRepositoryInterfaces(this.shouldConsiderNestedRepositories());
        scanner.setEnvironment(this.environment);
        scanner.setResourceLoader(loader);
        this.getExcludeFilters().forEach(it -> scanner.addExcludeFilter((TypeFilter)it));
        return Streamable.of(() -> this.getBasePackages().stream().flatMap(it -> scanner.findCandidateComponents((String)it).stream()));
    }

    @Override
    public Streamable<TypeFilter> getExcludeFilters() {
        return Streamable.empty();
    }

    @Override
    public String generateBeanName(BeanDefinition beanDefinition) {
        return this.beanNameGenerator.generateBeanName(beanDefinition);
    }

    protected Iterable<TypeFilter> getIncludeFilters() {
        return Collections.emptySet();
    }

    public boolean shouldConsiderNestedRepositories() {
        return false;
    }

    @Override
    public ImplementationDetectionConfiguration toImplementationDetectionConfiguration(MetadataReaderFactory factory) {
        return new SpringImplementationDetectionConfiguration(this, factory);
    }

    private class SpringImplementationDetectionConfiguration
    implements ImplementationDetectionConfiguration {
        private final RepositoryConfigurationSource source;
        private final MetadataReaderFactory metadataReaderFactory;

        SpringImplementationDetectionConfiguration(RepositoryConfigurationSource source, MetadataReaderFactory metadataReaderFactory) {
            this.source = source;
            this.metadataReaderFactory = metadataReaderFactory;
        }

        @Override
        public String getImplementationPostfix() {
            return this.source.getRepositoryImplementationPostfix().orElse(RepositoryConfigurationSourceSupport.DEFAULT_REPOSITORY_IMPL_POSTFIX);
        }

        @Override
        public Streamable<String> getBasePackages() {
            return this.source.getBasePackages();
        }

        @Override
        public Streamable<TypeFilter> getExcludeFilters() {
            return this.source.getExcludeFilters();
        }

        @Override
        public String generateBeanName(BeanDefinition definition) {
            return this.source.generateBeanName(definition);
        }

        @Override
        public MetadataReaderFactory getMetadataReaderFactory() {
            return this.metadataReaderFactory;
        }
    }
}

