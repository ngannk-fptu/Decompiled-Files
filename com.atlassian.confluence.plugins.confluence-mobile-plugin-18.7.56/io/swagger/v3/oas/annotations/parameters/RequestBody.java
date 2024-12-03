/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.parameters;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.Content;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestBody {
    public String description() default "";

    public Content[] content() default {};

    public boolean required() default false;

    public Extension[] extensions() default {};

    public String ref() default "";
}

