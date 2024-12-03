/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.security;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=SecurityRequirements.class)
@Inherited
public @interface SecurityRequirement {
    public String name();

    public String[] scopes() default {};
}

