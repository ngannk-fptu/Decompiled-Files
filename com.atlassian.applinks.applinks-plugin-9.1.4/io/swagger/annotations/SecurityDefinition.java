/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.BasicAuthDefinition;
import io.swagger.annotations.OAuth2Definition;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SecurityDefinition {
    public OAuth2Definition[] oAuth2Definitions() default {};

    @Deprecated
    public ApiKeyAuthDefinition[] apiKeyAuthDefintions() default {};

    public ApiKeyAuthDefinition[] apiKeyAuthDefinitions() default {};

    @Deprecated
    public BasicAuthDefinition[] basicAuthDefinions() default {};

    public BasicAuthDefinition[] basicAuthDefinitions() default {};
}

