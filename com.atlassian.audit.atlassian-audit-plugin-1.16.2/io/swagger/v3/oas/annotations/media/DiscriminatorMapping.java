/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.media;

import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.extensions.Extension;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface DiscriminatorMapping {
    public String value() default "";

    public Class<?> schema() default Void.class;

    @OpenAPI31
    public Extension[] extensions() default {};
}

