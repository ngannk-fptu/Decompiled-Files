/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.media;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.DependentRequired;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Schema {
    public Class<?> implementation() default Void.class;

    public Class<?> not() default Void.class;

    public Class<?>[] oneOf() default {};

    public Class<?>[] anyOf() default {};

    public Class<?>[] allOf() default {};

    public String name() default "";

    public String title() default "";

    public double multipleOf() default 0.0;

    public String maximum() default "";

    public boolean exclusiveMaximum() default false;

    public String minimum() default "";

    public boolean exclusiveMinimum() default false;

    public int maxLength() default 0x7FFFFFFF;

    public int minLength() default 0;

    public String pattern() default "";

    public int maxProperties() default 0;

    public int minProperties() default 0;

    public String[] requiredProperties() default {};

    @Deprecated
    public boolean required() default false;

    public RequiredMode requiredMode() default RequiredMode.AUTO;

    public String description() default "";

    public String format() default "";

    public String ref() default "";

    public boolean nullable() default false;

    @Deprecated
    public boolean readOnly() default false;

    @Deprecated
    public boolean writeOnly() default false;

    public AccessMode accessMode() default AccessMode.AUTO;

    public String example() default "";

    public ExternalDocumentation externalDocs() default @ExternalDocumentation;

    public boolean deprecated() default false;

    public String type() default "";

    public String[] allowableValues() default {};

    public String defaultValue() default "";

    public String discriminatorProperty() default "";

    public DiscriminatorMapping[] discriminatorMapping() default {};

    public boolean hidden() default false;

    public boolean enumAsRef() default false;

    public Class<?>[] subTypes() default {};

    public Extension[] extensions() default {};

    public Class<?>[] prefixItems() default {};

    @OpenAPI31
    public String[] types() default {};

    @OpenAPI31
    public int exclusiveMaximumValue() default 0;

    @OpenAPI31
    public int exclusiveMinimumValue() default 0;

    @OpenAPI31
    public Class<?> contains() default Void.class;

    @OpenAPI31
    public String $id() default "";

    @OpenAPI31
    public String $schema() default "";

    @OpenAPI31
    public String $anchor() default "";

    @OpenAPI31
    public String $vocabulary() default "";

    @OpenAPI31
    public String $dynamicAnchor() default "";

    @OpenAPI31
    public String contentEncoding() default "";

    @OpenAPI31
    public String contentMediaType() default "";

    @OpenAPI31
    public Class<?> contentSchema() default Void.class;

    @OpenAPI31
    public Class<?> propertyNames() default Void.class;

    @OpenAPI31
    public int maxContains() default 0x7FFFFFFF;

    @OpenAPI31
    public int minContains() default 0;

    public Class<?> additionalItems() default Void.class;

    public Class<?> unevaluatedItems() default Void.class;

    @OpenAPI31
    public Class<?> _if() default Void.class;

    @OpenAPI31
    public Class<?> _else() default Void.class;

    @OpenAPI31
    public Class<?> then() default Void.class;

    @OpenAPI31
    public String $comment() default "";

    public Class<?>[] exampleClasses() default {};

    public AdditionalPropertiesValue additionalProperties() default AdditionalPropertiesValue.USE_ADDITIONAL_PROPERTIES_ANNOTATION;

    @OpenAPI31
    public DependentRequired[] dependentRequiredMap() default {};

    @OpenAPI31
    public StringToClassMapItem[] dependentSchemas() default {};

    @OpenAPI31
    public StringToClassMapItem[] patternProperties() default {};

    public StringToClassMapItem[] properties() default {};

    @OpenAPI31
    public Class<?> unevaluatedProperties() default Void.class;

    public Class<?> additionalPropertiesSchema() default Void.class;

    @OpenAPI31
    public String[] examples() default {};

    @OpenAPI31
    public String _const() default "";

    public static enum RequiredMode {
        AUTO,
        REQUIRED,
        NOT_REQUIRED;

    }

    public static enum AdditionalPropertiesValue {
        TRUE,
        FALSE,
        USE_ADDITIONAL_PROPERTIES_ANNOTATION;

    }

    public static enum AccessMode {
        AUTO,
        READ_ONLY,
        WRITE_ONLY,
        READ_WRITE;

    }
}

