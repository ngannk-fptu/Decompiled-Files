/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanNameGenerator
 *  org.springframework.core.annotation.AliasFor
 */
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
@Repeatable(value=ComponentScans.class)
public @interface ComponentScan {
    @AliasFor(value="basePackages")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] basePackages() default {};

    public Class<?>[] basePackageClasses() default {};

    public Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    public Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

    public ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

    public String resourcePattern() default "**/*.class";

    public boolean useDefaultFilters() default true;

    public Filter[] includeFilters() default {};

    public Filter[] excludeFilters() default {};

    public boolean lazyInit() default false;

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={})
    public static @interface Filter {
        public FilterType type() default FilterType.ANNOTATION;

        @AliasFor(value="classes")
        public Class<?>[] value() default {};

        @AliasFor(value="value")
        public Class<?>[] classes() default {};

        public String[] pattern() default {};
    }
}

