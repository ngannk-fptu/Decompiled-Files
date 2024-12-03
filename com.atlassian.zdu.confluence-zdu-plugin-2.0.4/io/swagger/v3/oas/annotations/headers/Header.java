/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.headers;

import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Header {
    public String name();

    public String description() default "";

    public Schema schema() default @Schema;

    public boolean required() default false;

    public boolean deprecated() default false;

    public String ref() default "";
}

