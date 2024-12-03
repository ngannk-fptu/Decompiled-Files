/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ParserStrategyUtils;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
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
        for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
            for (TypeFilter typeFilter : this.typeFiltersFor(filter)) {
                scanner.addIncludeFilter(typeFilter);
            }
        }
        for (AnnotationAttributes filter : componentScan.getAnnotationArray("excludeFilters")) {
            for (TypeFilter typeFilter : this.typeFiltersFor(filter)) {
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

    private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {
        ArrayList<TypeFilter> typeFilters = new ArrayList<TypeFilter>();
        FilterType filterType = (FilterType)((Object)filterAttributes.getEnum("type"));
        block9: for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION: {
                    Assert.isAssignable(Annotation.class, filterClass, "@ComponentScan ANNOTATION type filter requires an annotation type");
                    Class<?> annotationType = filterClass;
                    typeFilters.add(new AnnotationTypeFilter(annotationType));
                    continue block9;
                }
                case ASSIGNABLE_TYPE: {
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    continue block9;
                }
                case CUSTOM: {
                    Assert.isAssignable(TypeFilter.class, filterClass, "@ComponentScan CUSTOM type filter requires a TypeFilter implementation");
                    TypeFilter filter = BeanUtils.instantiateClass(filterClass, TypeFilter.class);
                    ParserStrategyUtils.invokeAwareMethods(filter, this.environment, this.resourceLoader, this.registry);
                    typeFilters.add(filter);
                    continue block9;
                }
                default: {
                    throw new IllegalArgumentException("Filter type not supported with Class value: " + (Object)((Object)filterType));
                }
            }
        }
        block10: for (String expression : filterAttributes.getStringArray("pattern")) {
            switch (filterType) {
                case ASPECTJ: {
                    typeFilters.add(new AspectJTypeFilter(expression, this.resourceLoader.getClassLoader()));
                    continue block10;
                }
                case REGEX: {
                    typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(expression)));
                    continue block10;
                }
                default: {
                    throw new IllegalArgumentException("Filter type not supported with String pattern: " + (Object)((Object)filterType));
                }
            }
        }
        return typeFilters;
    }
}

