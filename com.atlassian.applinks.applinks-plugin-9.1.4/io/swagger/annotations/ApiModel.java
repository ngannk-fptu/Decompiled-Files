/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface ApiModel {
    public String value() default "";

    public String description() default "";

    public Class<?> parent() default Void.class;

    public String discriminator() default "";

    public Class<?>[] subTypes() default {};

    public String reference() default "";
}

