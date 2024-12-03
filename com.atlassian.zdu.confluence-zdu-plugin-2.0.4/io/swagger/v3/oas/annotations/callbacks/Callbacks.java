/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.v3.oas.annotations.callbacks;

import io.swagger.v3.oas.annotations.callbacks.Callback;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Callbacks {
    public Callback[] value() default {};
}

