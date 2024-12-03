/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.context.annotation.AnnotationConfigUtils
 *  org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.AssignableTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.util.ClassUtils;
import org.springframework.util.Assert;

class RepositoryComponentProvider
extends ClassPathScanningCandidateComponentProvider {
    private boolean considerNestedRepositoryInterfaces;
    private BeanDefinitionRegistry registry;

    public RepositoryComponentProvider(Iterable<? extends TypeFilter> includeFilters, BeanDefinitionRegistry registry) {
        super(false);
        Assert.notNull(includeFilters, (String)"Include filters must not be null!");
        Assert.notNull((Object)registry, (String)"BeanDefinitionRegistry must not be null!");
        this.registry = registry;
        if (includeFilters.iterator().hasNext()) {
            for (TypeFilter typeFilter : includeFilters) {
                this.addIncludeFilter(typeFilter);
            }
        } else {
            super.addIncludeFilter((TypeFilter)new InterfaceTypeFilter(Repository.class));
            super.addIncludeFilter((TypeFilter)new AnnotationTypeFilter(RepositoryDefinition.class, true, true));
        }
        this.addExcludeFilter((TypeFilter)new AnnotationTypeFilter(NoRepositoryBean.class));
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        ArrayList<TypeFilter> filterPlusInterface = new ArrayList<TypeFilter>(2);
        filterPlusInterface.add(includeFilter);
        filterPlusInterface.add((TypeFilter)new InterfaceTypeFilter(Repository.class));
        super.addIncludeFilter((TypeFilter)new AllTypeFilter(filterPlusInterface));
        ArrayList<TypeFilter> filterPlusAnnotation = new ArrayList<TypeFilter>(2);
        filterPlusAnnotation.add(includeFilter);
        filterPlusAnnotation.add((TypeFilter)new AnnotationTypeFilter(RepositoryDefinition.class, true, true));
        super.addIncludeFilter((TypeFilter)new AllTypeFilter(filterPlusAnnotation));
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean isNonRepositoryInterface = !ClassUtils.isGenericRepositoryInterface(beanDefinition.getBeanClassName());
        boolean isTopLevelType = !beanDefinition.getMetadata().hasEnclosingClass();
        boolean isConsiderNestedRepositories = this.isConsiderNestedRepositoryInterfaces();
        return isNonRepositoryInterface && (isTopLevelType || isConsiderNestedRepositories);
    }

    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set candidates = super.findCandidateComponents(basePackage);
        for (BeanDefinition candidate : candidates) {
            if (!(candidate instanceof AnnotatedBeanDefinition)) continue;
            AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition)((AnnotatedBeanDefinition)candidate));
        }
        return candidates;
    }

    @Nonnull
    protected BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    public boolean isConsiderNestedRepositoryInterfaces() {
        return this.considerNestedRepositoryInterfaces;
    }

    public void setConsiderNestedRepositoryInterfaces(boolean considerNestedRepositoryInterfaces) {
        this.considerNestedRepositoryInterfaces = considerNestedRepositoryInterfaces;
    }

    private static class AllTypeFilter
    implements TypeFilter {
        private final List<TypeFilter> delegates;

        public AllTypeFilter(List<TypeFilter> delegates) {
            Assert.notNull(delegates, (String)"TypeFilter deleages must not be null!");
            this.delegates = delegates;
        }

        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
            for (TypeFilter filter : this.delegates) {
                if (filter.match(metadataReader, metadataReaderFactory)) continue;
                return false;
            }
            return true;
        }
    }

    private static class InterfaceTypeFilter
    extends AssignableTypeFilter {
        public InterfaceTypeFilter(Class<?> targetType) {
            super(targetType);
        }

        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
            return metadataReader.getClassMetadata().isInterface() && super.match(metadataReader, metadataReaderFactory);
        }
    }
}

