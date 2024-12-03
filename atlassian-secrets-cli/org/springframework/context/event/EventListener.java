/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface EventListener {
    @AliasFor(value="classes")
    public Class<?>[] value() default {};

    @AliasFor(value="value")
    public Class<?>[] classes() default {};

    public String condition() default "";
}

