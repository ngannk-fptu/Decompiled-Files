/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.spring.scanner.annotation.imports;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ComponentImport {
    public String value() default "";
}

