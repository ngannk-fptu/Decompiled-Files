/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.media;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.PatternProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(value=PatternProperties.class)
public @interface PatternProperty {
    public String regex() default "";

    public Schema schema() default @Schema;

    public ArraySchema array() default @ArraySchema;
}

