/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.config;

import java.beans.Introspector;
import java.io.IOException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.ImplementationLookupConfiguration;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

class DefaultImplementationLookupConfiguration
implements ImplementationLookupConfiguration {
    private final ImplementationDetectionConfiguration config;
    private final String interfaceName;
    private final String beanName;

    DefaultImplementationLookupConfiguration(ImplementationDetectionConfiguration config, String interfaceName) {
        Assert.notNull((Object)config, (String)"ImplementationDetectionConfiguration must not be null!");
        Assert.hasText((String)interfaceName, (String)"Interface name must not be null or empty!");
        this.config = config;
        this.interfaceName = interfaceName;
        this.beanName = Introspector.decapitalize(ClassUtils.getShortName((String)interfaceName).concat(config.getImplementationPostfix()));
    }

    @Override
    public String getImplementationBeanName() {
        return this.beanName;
    }

    @Override
    public String getImplementationPostfix() {
        return this.config.getImplementationPostfix();
    }

    @Override
    public Streamable<TypeFilter> getExcludeFilters() {
        return this.config.getExcludeFilters().and((TypeFilter[])new TypeFilter[]{new AnnotationTypeFilter(NoRepositoryBean.class)});
    }

    @Override
    public MetadataReaderFactory getMetadataReaderFactory() {
        return this.config.getMetadataReaderFactory();
    }

    @Override
    public Streamable<String> getBasePackages() {
        return Streamable.of(ClassUtils.getPackageName((String)this.interfaceName));
    }

    @Override
    public String getImplementationClassName() {
        return this.getLocalName(this.interfaceName).concat(this.getImplementationPostfix());
    }

    @Override
    public boolean hasMatchingBeanName(BeanDefinition definition) {
        Assert.notNull((Object)definition, (String)"BeanDefinition must not be null!");
        return this.beanName != null && this.beanName.equals(this.config.generateBeanName(definition));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean matches(BeanDefinition definition) {
        Assert.notNull((Object)definition, (String)"BeanDefinition must not be null!");
        String beanClassName = definition.getBeanClassName();
        if (beanClassName == null) return false;
        if (this.isExcluded(beanClassName, this.getExcludeFilters())) {
            return false;
        }
        String beanPackage = ClassUtils.getPackageName((String)beanClassName);
        String localName = this.getLocalName(beanClassName);
        if (!localName.equals(this.getImplementationClassName())) return false;
        if (!this.getBasePackages().stream().anyMatch(beanPackage::startsWith)) return false;
        return true;
    }

    private String getLocalName(String className) {
        String shortName = ClassUtils.getShortName((String)className);
        return shortName.substring(shortName.lastIndexOf(46) + 1);
    }

    private boolean isExcluded(String beanClassName, Streamable<TypeFilter> filters) {
        try {
            MetadataReader reader = this.getMetadataReaderFactory().getMetadataReader(beanClassName);
            return filters.stream().anyMatch(it -> this.matches((TypeFilter)it, reader));
        }
        catch (IOException o_O) {
            return true;
        }
    }

    private boolean matches(TypeFilter filter, MetadataReader reader) {
        try {
            return filter.match(reader, this.getMetadataReaderFactory());
        }
        catch (IOException e) {
            return false;
        }
    }
}

