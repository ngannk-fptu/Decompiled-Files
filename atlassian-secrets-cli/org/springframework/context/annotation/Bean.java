/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    @AliasFor(value="name")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] name() default {};

    public Autowire autowire() default Autowire.NO;

    public String initMethod() default "";

    public String destroyMethod() default "(inferred)";
}

