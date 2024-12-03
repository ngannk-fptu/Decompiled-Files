/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.security;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface OAuthScope {
    public String name() default "";

    public String description() default "";
}

