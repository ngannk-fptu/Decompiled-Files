/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.media;

import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.DependentSchema;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Content {
    public String mediaType() default "";

    public ExampleObject[] examples() default {};

    public Schema schema() default @Schema;

    public SchemaProperty[] schemaProperties() default {};

    public Schema additionalPropertiesSchema() default @Schema;

    public ArraySchema array() default @ArraySchema;

    public Encoding[] encoding() default {};

    public Extension[] extensions() default {};

    @OpenAPI31
    public DependentSchema[] dependentSchemas() default {};

    @OpenAPI31
    public Schema contentSchema() default @Schema;

    @OpenAPI31
    public Schema propertyNames() default @Schema;

    @OpenAPI31
    public Schema _if() default @Schema;

    @OpenAPI31
    public Schema _then() default @Schema;

    @OpenAPI31
    public Schema _else() default @Schema;

    public Schema not() default @Schema;

    public Schema[] oneOf() default {};

    public Schema[] anyOf() default {};

    public Schema[] allOf() default {};
}

