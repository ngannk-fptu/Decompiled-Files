/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.config;

import java.util.Optional;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.config.ConfigurationUtils;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.ImplementationLookupConfiguration;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class DefaultRepositoryConfiguration<T extends RepositoryConfigurationSource>
implements RepositoryConfiguration<T> {
    public static final String DEFAULT_REPOSITORY_IMPLEMENTATION_POSTFIX = "Impl";
    public static final QueryLookupStrategy.Key DEFAULT_QUERY_LOOKUP_STRATEGY = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;
    private final T configurationSource;
    private final BeanDefinition definition;
    private final RepositoryConfigurationExtension extension;

    public DefaultRepositoryConfiguration(T configurationSource, BeanDefinition definition, RepositoryConfigurationExtension extension) {
        this.configurationSource = configurationSource;
        this.definition = definition;
        this.extension = extension;
    }

    public String getBeanId() {
        return StringUtils.uncapitalize((String)ClassUtils.getShortName((String)this.getRepositoryBaseClassName().orElseThrow(() -> new IllegalStateException("Can't create bean identifier without a repository base class defined!"))));
    }

    @Override
    public Object getQueryLookupStrategyKey() {
        return this.configurationSource.getQueryLookupStrategyKey().orElse((Object)DEFAULT_QUERY_LOOKUP_STRATEGY);
    }

    @Override
    public Streamable<String> getBasePackages() {
        return this.configurationSource.getBasePackages();
    }

    @Override
    public Streamable<String> getImplementationBasePackages() {
        return Streamable.of(ClassUtils.getPackageName((String)this.getRepositoryInterface()));
    }

    @Override
    public String getRepositoryInterface() {
        return ConfigurationUtils.getRequiredBeanClassName(this.definition);
    }

    public RepositoryConfigurationSource getConfigSource() {
        return this.configurationSource;
    }

    @Override
    public Optional<String> getNamedQueriesLocation() {
        return this.configurationSource.getNamedQueryLocation();
    }

    public String getImplementationClassName() {
        return ClassUtils.getShortName((String)this.getRepositoryInterface()).concat(this.configurationSource.getRepositoryImplementationPostfix().orElse(DEFAULT_REPOSITORY_IMPLEMENTATION_POSTFIX));
    }

    public String getImplementationBeanName() {
        return this.configurationSource.generateBeanName(this.definition) + this.configurationSource.getRepositoryImplementationPostfix().orElse(DEFAULT_REPOSITORY_IMPLEMENTATION_POSTFIX);
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.configurationSource.getSource();
    }

    @Override
    public T getConfigurationSource() {
        return this.configurationSource;
    }

    @Override
    public Optional<String> getRepositoryBaseClassName() {
        return this.configurationSource.getRepositoryBaseClassName();
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return this.configurationSource.getRepositoryFactoryBeanClassName().orElseGet(this.extension::getRepositoryFactoryBeanClassName);
    }

    @Override
    public boolean isLazyInit() {
        return this.definition.isLazyInit() || !this.configurationSource.getBootstrapMode().equals((Object)BootstrapMode.DEFAULT);
    }

    @Override
    public boolean isPrimary() {
        return this.definition.isPrimary();
    }

    @Override
    public Streamable<TypeFilter> getExcludeFilters() {
        return this.configurationSource.getExcludeFilters();
    }

    @Override
    public ImplementationDetectionConfiguration toImplementationDetectionConfiguration(MetadataReaderFactory factory) {
        Assert.notNull((Object)factory, (String)"MetadataReaderFactory must not be null!");
        return this.configurationSource.toImplementationDetectionConfiguration(factory);
    }

    @Override
    public ImplementationLookupConfiguration toLookupConfiguration(MetadataReaderFactory factory) {
        Assert.notNull((Object)factory, (String)"MetadataReaderFactory must not be null!");
        return this.toImplementationDetectionConfiguration(factory).forRepositoryConfiguration(this);
    }

    @Override
    @NonNull
    public String getResourceDescription() {
        return String.format("%s defined in %s", this.getRepositoryInterface(), this.configurationSource.getResourceDescription());
    }
}

