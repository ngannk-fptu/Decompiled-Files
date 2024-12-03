/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@Documented
public @interface AliasFor {
    @AliasFor(value="attribute")
    public String value() default "";

    @AliasFor(value="value")
    public String attribute() default "";

    public Class<? extends Annotation> annotation() default Annotation.class;
}

