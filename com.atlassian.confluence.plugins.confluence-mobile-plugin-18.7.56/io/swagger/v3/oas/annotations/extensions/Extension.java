/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.extensions;

import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.extensions.Extensions;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Extensions.class)
public @interface Extension {
    public String name() default "";

    public ExtensionProperty[] properties();
}

