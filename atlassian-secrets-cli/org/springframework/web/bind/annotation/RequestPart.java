/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface RequestPart {
    @AliasFor(value="name")
    public String value() default "";

    @AliasFor(value="value")
    public String name() default "";

    public boolean required() default true;
}

