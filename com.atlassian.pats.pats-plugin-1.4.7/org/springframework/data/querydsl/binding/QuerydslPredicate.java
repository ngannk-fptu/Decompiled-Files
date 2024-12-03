/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.querydsl.binding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;

@Target(value={ElementType.PARAMETER, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface QuerydslPredicate {
    public Class<?> root() default Object.class;

    public Class<? extends QuerydslBinderCustomizer> bindings() default QuerydslBinderCustomizer.class;
}

