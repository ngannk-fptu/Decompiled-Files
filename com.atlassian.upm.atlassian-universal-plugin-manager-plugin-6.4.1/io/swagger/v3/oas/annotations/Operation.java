/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Operation {
    public String method() default "";

    public String[] tags() default {};

    public String summary() default "";

    public String description() default "";

    public RequestBody requestBody() default @RequestBody;

    public ExternalDocumentation externalDocs() default @ExternalDocumentation;

    public String operationId() default "";

    public Parameter[] parameters() default {};

    public ApiResponse[] responses() default {};

    public boolean deprecated() default false;

    public SecurityRequirement[] security() default {};

    public Server[] servers() default {};

    public Extension[] extensions() default {};

    public boolean hidden() default false;

    public boolean ignoreJsonView() default false;
}

