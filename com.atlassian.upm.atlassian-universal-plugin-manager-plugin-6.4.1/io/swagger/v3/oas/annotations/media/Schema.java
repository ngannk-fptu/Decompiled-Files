/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.media;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.extensions.Extension;
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

    public AdditionalPropertiesValue additionalProperties() default AdditionalPropertiesValue.USE_ADDITIONAL_PROPERTIES_ANNOTATION;

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

