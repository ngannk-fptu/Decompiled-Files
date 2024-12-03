/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.tags;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.tags.Tags;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Tags.class)
@Inherited
public @interface Tag {
    public String name();

    public String description() default "";

    public ExternalDocumentation externalDocs() default @ExternalDocumentation;

    public Extension[] extensions() default {};
}

