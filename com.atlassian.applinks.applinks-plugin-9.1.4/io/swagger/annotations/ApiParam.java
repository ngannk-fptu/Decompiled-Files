/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ApiParam {
    public String name() default "";

    public String value() default "";

    public String defaultValue() default "";

    public String allowableValues() default "";

    public boolean required() default false;

    public String access() default "";

    public boolean allowMultiple() default false;

    public boolean hidden() default false;

    public String example() default "";

    public Example examples() default @Example(value={@ExampleProperty(mediaType="", value="")});

    public String type() default "";

    public String format() default "";

    public boolean allowEmptyValue() default false;

    public boolean readOnly() default false;

    public String collectionFormat() default "";
}

