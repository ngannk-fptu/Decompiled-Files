/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.annotation.TypeFilterUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

class ComponentScanAnnotationParser {
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final BeanNameGenerator beanNameGenerator;
    private final BeanDefinitionRegistry registry;

    public ComponentScanAnnotationParser(Environment environment2, ResourceLoader resourceLoader, BeanNameGenerator beanNameGenerator, BeanDefinitionRegistry registry) {
        this.environment = environment2;
        this.resourceLoader = resourceLoader;
        this.beanNameGenerator = beanNameGenerator;
        this.registry = registry;
    }

    public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
        List<TypeFilter> typeFilters;
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry, componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);
        Class generatorClass = componentScan.getClass("nameGenerator");
        boolean useInheritedGenerator = BeanNameGenerator.class == generatorClass;
        scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator : (BeanNameGenerator)BeanUtils.instantiateClass(generatorClass));
        ScopedProxyMode scopedProxyMode = (ScopedProxyMode)((Object)componentScan.getEnum("scopedProxy"));
        if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
            scanner.setScopedProxyMode(scopedProxyMode);
        } else {
            AnnotationAttributes[] resolverClass = componentScan.getClass("scopeResolver");
            scanner.setScopeMetadataResolver((ScopeMetadataResolver)BeanUtils.instantiateClass(resolverClass));
        }
        scanner.setResourcePattern(componentScan.getString("resourcePattern"));
        for (AnnotationAttributes includeFilterAttributes : componentScan.getAnnotationArray("includeFilters")) {
            typeFilters = TypeFilterUtils.createTypeFiltersFor(includeFilterAttributes, this.environment, this.resourceLoader, this.registry);
            for (TypeFilter typeFilter : typeFilters) {
                scanner.addIncludeFilter(typeFilter);
            }
        }
        for (AnnotationAttributes excludeFilterAttributes : componentScan.getAnnotationArray("excludeFilters")) {
            typeFilters = TypeFilterUtils.createTypeFiltersFor(excludeFilterAttributes, this.environment, this.resourceLoader, this.registry);
            for (TypeFilter typeFilter : typeFilters) {
                scanner.addExcludeFilter(typeFilter);
            }
        }
        boolean lazyInit = componentScan.getBoolean("lazyInit");
        if (lazyInit) {
            scanner.getBeanDefinitionDefaults().setLazyInit(true);
        }
        LinkedHashSet<String> basePackages = new LinkedHashSet<String>();
        String[] basePackagesArray = componentScan.getStringArray("basePackages");
        for (String pkg : basePackagesArray) {
            String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg), ",; \t\n");
            Collections.addAll(basePackages, tokenized);
        }
        for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(declaringClass));
        }
        scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false){

            @Override
            protected boolean matchClassName(String className) {
                return declaringClass.equals(className);
            }
        });
        return scanner.doScan(StringUtils.toStringArray(basePackages));
    }
}

