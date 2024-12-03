/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.links;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface LinkParameter {
    public String name() default "";

    public String expression() default "";
}

