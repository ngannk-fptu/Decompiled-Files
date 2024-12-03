/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.info;

import io.swagger.v3.oas.annotations.extensions.Extension;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface License {
    public String name() default "";

    public String url() default "";

    public Extension[] extensions() default {};
}

