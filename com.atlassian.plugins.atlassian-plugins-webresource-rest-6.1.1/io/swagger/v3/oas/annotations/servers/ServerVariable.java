/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.servers;

import io.swagger.v3.oas.annotations.extensions.Extension;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface ServerVariable {
    public String name();

    public String[] allowableValues() default {""};

    public String defaultValue();

    public String description() default "";

    public Extension[] extensions() default {};
}

