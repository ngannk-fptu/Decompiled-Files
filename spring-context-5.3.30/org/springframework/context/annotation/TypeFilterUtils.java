/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.AspectJTypeFilter
 *  org.springframework.core.type.filter.AssignableTypeFilter
 *  org.springframework.core.type.filter.RegexPatternTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.util.Assert
 */
package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ParserStrategyUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

public abstract class TypeFilterUtils {
    public static List<TypeFilter> createTypeFiltersFor(AnnotationAttributes filterAttributes, Environment environment2, ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {
        ArrayList<TypeFilter> typeFilters = new ArrayList<TypeFilter>();
        FilterType filterType = (FilterType)filterAttributes.getEnum("type");
        block9: for (Class filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION: {
                    Assert.isAssignable(Annotation.class, (Class)filterClass, (String)"@ComponentScan ANNOTATION type filter requires an annotation type");
                    Class annotationType = filterClass;
                    typeFilters.add((TypeFilter)new AnnotationTypeFilter(annotationType));
                    continue block9;
                }
                case ASSIGNABLE_TYPE: {
                    typeFilters.add((TypeFilter)new AssignableTypeFilter(filterClass));
                    continue block9;
                }
                case CUSTOM: {
                    Assert.isAssignable(TypeFilter.class, (Class)filterClass, (String)"@ComponentScan CUSTOM type filter requires a TypeFilter implementation");
                    TypeFilter filter = ParserStrategyUtils.instantiateClass(filterClass, TypeFilter.class, environment2, resourceLoader, registry);
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
                    typeFilters.add((TypeFilter)new AspectJTypeFilter(expression, resourceLoader.getClassLoader()));
                    continue block10;
                }
                case REGEX: {
                    typeFilters.add((TypeFilter)new RegexPatternTypeFilter(Pattern.compile(expression)));
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

