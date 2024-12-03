/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations;

import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Parameters.class)
@Inherited
public @interface Parameter {
    public String name() default "";

    public ParameterIn in() default ParameterIn.DEFAULT;

    public String description() default "";

    public boolean required() default false;

    public boolean deprecated() default false;

    public boolean allowEmptyValue() default false;

    public ParameterStyle style() default ParameterStyle.DEFAULT;

    public Explode explode() default Explode.DEFAULT;

    public boolean allowReserved() default false;

    public Schema schema() default @Schema;

    public ArraySchema array() default @ArraySchema;

    public Content[] content() default {};

    public boolean hidden() default false;

    public ExampleObject[] examples() default {};

    public String example() default "";

    public Extension[] extensions() default {};

    public String ref() default "";
}

