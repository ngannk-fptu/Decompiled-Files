/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.callbacks;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.callbacks.Callbacks;
import io.swagger.v3.oas.annotations.extensions.Extension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=Callbacks.class)
@Inherited
public @interface Callback {
    public String name() default "";

    public String callbackUrlExpression() default "";

    public Operation[] operation() default {};

    public Extension[] extensions() default {};

    public String ref() default "";
}

