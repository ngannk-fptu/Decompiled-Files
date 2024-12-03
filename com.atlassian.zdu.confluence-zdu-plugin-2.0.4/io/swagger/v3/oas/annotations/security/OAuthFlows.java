/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.security;

import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface OAuthFlows {
    public OAuthFlow implicit() default @OAuthFlow;

    public OAuthFlow password() default @OAuthFlow;

    public OAuthFlow clientCredentials() default @OAuthFlow;

    public OAuthFlow authorizationCode() default @OAuthFlow;

    public Extension[] extensions() default {};
}

