/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.security;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface OAuthFlow {
    public String authorizationUrl() default "";

    public String tokenUrl() default "";

    public String refreshUrl() default "";

    public OAuthScope[] scopes() default {};

    public Extension[] extensions() default {};
}

