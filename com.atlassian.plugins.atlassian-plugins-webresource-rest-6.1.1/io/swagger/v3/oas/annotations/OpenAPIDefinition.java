/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.PACKAGE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface OpenAPIDefinition {
    public Info info() default @Info;

    public Tag[] tags() default {};

    public Server[] servers() default {};

    public SecurityRequirement[] security() default {};

    public ExternalDocumentation externalDocs() default @ExternalDocumentation;

    public Extension[] extensions() default {};
}

