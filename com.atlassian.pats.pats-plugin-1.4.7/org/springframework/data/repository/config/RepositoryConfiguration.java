/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.config;

import java.util.Optional;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.ImplementationLookupConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;

public interface RepositoryConfiguration<T extends RepositoryConfigurationSource> {
    public Streamable<String> getBasePackages();

    public Streamable<String> getImplementationBasePackages();

    public String getRepositoryInterface();

    public Object getQueryLookupStrategyKey();

    public Optional<String> getNamedQueriesLocation();

    public Optional<String> getRepositoryBaseClassName();

    public String getRepositoryFactoryBeanClassName();

    @Nullable
    public Object getSource();

    public T getConfigurationSource();

    public boolean isLazyInit();

    public boolean isPrimary();

    public Streamable<TypeFilter> getExcludeFilters();

    public ImplementationDetectionConfiguration toImplementationDetectionConfiguration(MetadataReaderFactory var1);

    public ImplementationLookupConfiguration toLookupConfiguration(MetadataReaderFactory var1);

    @Nullable
    public String getResourceDescription();
}

