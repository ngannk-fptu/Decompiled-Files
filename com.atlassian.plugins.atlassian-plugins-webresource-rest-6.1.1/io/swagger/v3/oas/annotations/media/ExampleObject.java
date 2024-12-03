/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.media;

import io.swagger.v3.oas.annotations.extensions.Extension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface ExampleObject {
    public String name() default "";

    public String summary() default "";

    public String value() default "";

    public String externalValue() default "";

    public Extension[] extensions() default {};

    public String ref() default "";

    public String description() default "";
}

