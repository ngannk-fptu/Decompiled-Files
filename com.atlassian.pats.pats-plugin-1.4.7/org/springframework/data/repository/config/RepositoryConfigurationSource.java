/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import java.util.Optional;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public interface RepositoryConfigurationSource {
    @Nullable
    public Object getSource();

    public Streamable<String> getBasePackages();

    public Optional<Object> getQueryLookupStrategyKey();

    public Optional<String> getRepositoryImplementationPostfix();

    public Optional<String> getNamedQueryLocation();

    public Optional<String> getRepositoryBaseClassName();

    public Optional<String> getRepositoryFactoryBeanClassName();

    public Streamable<BeanDefinition> getCandidates(ResourceLoader var1);

    public Optional<String> getAttribute(String var1);

    public <T> Optional<T> getAttribute(String var1, Class<T> var2);

    default public <T> T getRequiredAttribute(String name, Class<T> type) {
        Assert.hasText((String)name, (String)"Attribute name must not be null or empty!");
        return this.getAttribute(name, type).orElseThrow(() -> new IllegalArgumentException(String.format("No attribute named %s found!", name)));
    }

    public boolean usesExplicitFilters();

    public Streamable<TypeFilter> getExcludeFilters();

    public String generateBeanName(BeanDefinition var1);

    public ImplementationDetectionConfiguration toImplementationDetectionConfiguration(MetadataReaderFactory var1);

    public BootstrapMode getBootstrapMode();

    @Nullable
    public String getResourceDescription();
}

