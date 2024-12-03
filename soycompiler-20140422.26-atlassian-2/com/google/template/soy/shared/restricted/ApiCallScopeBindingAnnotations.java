/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.BindingAnnotation
 */
package com.google.template.soy.shared.restricted;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ApiCallScopeBindingAnnotations {
    private ApiCallScopeBindingAnnotations() {
    }

    @BindingAnnotation
    @Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface LocaleString {
    }

    @BindingAnnotation
    @Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface IsUsingIjData {
    }

    @BindingAnnotation
    @Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface ApiCall {
    }
}

