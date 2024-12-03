/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.context.annotation.BeanMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

final class ConfigurationClass {
    private final AnnotationMetadata metadata;
    private final Resource resource;
    @Nullable
    private String beanName;
    private final Set<ConfigurationClass> importedBy = new LinkedHashSet<ConfigurationClass>(1);
    private final Set<BeanMethod> beanMethods = new LinkedHashSet<BeanMethod>();
    private final Map<String, Class<? extends BeanDefinitionReader>> importedResources = new LinkedHashMap<String, Class<? extends BeanDefinitionReader>>();
    private final Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> importBeanDefinitionRegistrars = new LinkedHashMap<ImportBeanDefinitionRegistrar, AnnotationMetadata>();
    final Set<String> skippedBeanMethods = new HashSet<String>();

    ConfigurationClass(MetadataReader metadataReader, String beanName) {
        Assert.notNull((Object)beanName, "Bean name must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.beanName = beanName;
    }

    ConfigurationClass(MetadataReader metadataReader, @Nullable ConfigurationClass importedBy) {
        this.metadata = metadataReader.getAnnotationMetadata();
        this.resource = metadataReader.getResource();
        this.importedBy.add(importedBy);
    }

    ConfigurationClass(Class<?> clazz, String beanName) {
        Assert.notNull((Object)beanName, "Bean name must not be null");
        this.metadata = AnnotationMetadata.introspect(clazz);
        this.resource = new DescriptiveResource(clazz.getName());
        this.beanName = beanName;
    }

    ConfigurationClass(Class<?> clazz, @Nullable ConfigurationClass importedBy) {
        this.metadata = AnnotationMetadata.introspect(clazz);
        this.resource = new DescriptiveResource(clazz.getName());
        this.importedBy.add(importedBy);
    }

    ConfigurationClass(AnnotationMetadata metadata, String beanName) {
        Assert.notNull((Object)beanName, "Bean name must not be null");
        this.metadata = metadata;
        this.resource = new DescriptiveResource(metadata.getClassName());
        this.beanName = beanName;
    }

    AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    Resource getResource() {
        return this.resource;
    }

    String getSimpleName() {
        return ClassUtils.getShortName(this.getMetadata().getClassName());
    }

    void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    public boolean isImported() {
        return !this.importedBy.isEmpty();
    }

    void mergeImportedBy(ConfigurationClass otherConfigClass) {
        this.importedBy.addAll(otherConfigClass.importedBy);
    }

    Set<ConfigurationClass> getImportedBy() {
        return this.importedBy;
    }

    void addBeanMethod(BeanMethod method) {
        this.beanMethods.add(method);
    }

    Set<BeanMethod> getBeanMethods() {
        return this.beanMethods;
    }

    void addImportedResource(String importedResource, Class<? extends BeanDefinitionReader> readerClass) {
        this.importedResources.put(importedResource, readerClass);
    }

    void addImportBeanDefinitionRegistrar(ImportBeanDefinitionRegistrar registrar, AnnotationMetadata importingClassMetadata) {
        this.importBeanDefinitionRegistrars.put(registrar, importingClassMetadata);
    }

    Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> getImportBeanDefinitionRegistrars() {
        return this.importBeanDefinitionRegistrars;
    }

    Map<String, Class<? extends BeanDefinitionReader>> getImportedResources() {
        return this.importedResources;
    }

    void validate(ProblemReporter problemReporter) {
        Map<String, Object> attributes = this.metadata.getAnnotationAttributes(Configuration.class.getName());
        if (attributes != null && ((Boolean)attributes.get("proxyBeanMethods")).booleanValue()) {
            if (this.metadata.isFinal()) {
                problemReporter.error(new FinalConfigurationProblem());
            }
            for (BeanMethod beanMethod : this.beanMethods) {
                beanMethod.validate(problemReporter);
            }
        }
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof ConfigurationClass && this.getMetadata().getClassName().equals(((ConfigurationClass)other).getMetadata().getClassName());
    }

    public int hashCode() {
        return this.getMetadata().getClassName().hashCode();
    }

    public String toString() {
        return "ConfigurationClass: beanName '" + this.beanName + "', " + this.resource;
    }

    private class FinalConfigurationProblem
    extends Problem {
        FinalConfigurationProblem() {
            super(String.format("@Configuration class '%s' may not be final. Remove the final modifier to continue.", ConfigurationClass.this.getSimpleName()), new Location(ConfigurationClass.this.getResource(), ConfigurationClass.this.getMetadata()));
        }
    }
}

