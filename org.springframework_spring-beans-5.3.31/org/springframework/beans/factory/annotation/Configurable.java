/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Autowire;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Configurable {
    public String value() default "";

    public Autowire autowire() default Autowire.NO;

    public boolean dependencyCheck() default false;

    public boolean preConstruction() default false;
}

