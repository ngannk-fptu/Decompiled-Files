/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ResponseHeader {
    public String name() default "";

    public String description() default "";

    public Class<?> response() default Void.class;

    public String responseContainer() default "";
}

