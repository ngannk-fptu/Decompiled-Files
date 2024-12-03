/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.security;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=SecuritySchemes.class)
@Inherited
public @interface SecurityScheme {
    public SecuritySchemeType type();

    public String name() default "";

    public String description() default "";

    public String paramName() default "";

    public SecuritySchemeIn in() default SecuritySchemeIn.DEFAULT;

    public String scheme() default "";

    public String bearerFormat() default "";

    public OAuthFlows flows() default @OAuthFlows;

    public String openIdConnectUrl() default "";

    public Extension[] extensions() default {};

    public String ref() default "";
}

