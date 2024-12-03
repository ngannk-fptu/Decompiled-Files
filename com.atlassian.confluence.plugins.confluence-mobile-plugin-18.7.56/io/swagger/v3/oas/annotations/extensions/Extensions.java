/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.extensions;

import io.swagger.v3.oas.annotations.extensions.Extension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Extensions {
    public Extension[] value() default {};
}

