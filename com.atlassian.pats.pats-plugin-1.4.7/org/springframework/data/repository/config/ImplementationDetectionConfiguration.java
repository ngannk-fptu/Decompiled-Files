/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.config;

import java.beans.Introspector;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.config.DefaultImplementationLookupConfiguration;
import org.springframework.data.repository.config.ImplementationLookupConfiguration;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public interface ImplementationDetectionConfiguration {
    public String getImplementationPostfix();

    public Streamable<String> getBasePackages();

    public Streamable<TypeFilter> getExcludeFilters();

    public MetadataReaderFactory getMetadataReaderFactory();

    default public String generateBeanName(BeanDefinition definition) {
        Assert.notNull((Object)definition, (String)"BeanDefinition must not be null!");
        String beanName = definition.getBeanClassName();
        if (beanName == null) {
            throw new IllegalStateException("Cannot generate bean name for BeanDefinition without bean class name!");
        }
        return Introspector.decapitalize(ClassUtils.getShortName((String)beanName));
    }

    default public ImplementationLookupConfiguration forFragment(String fragmentInterfaceName) {
        Assert.hasText((String)fragmentInterfaceName, (String)"Fragment interface name must not be null or empty!");
        return new DefaultImplementationLookupConfiguration(this, fragmentInterfaceName);
    }

    default public ImplementationLookupConfiguration forRepositoryConfiguration(final RepositoryConfiguration<?> config) {
        Assert.notNull(config, (String)"RepositoryConfiguration must not be null!");
        return new DefaultImplementationLookupConfiguration(this, config.getRepositoryInterface()){

            @Override
            public Streamable<String> getBasePackages() {
                return config.getImplementationBasePackages();
            }
        };
    }
}

