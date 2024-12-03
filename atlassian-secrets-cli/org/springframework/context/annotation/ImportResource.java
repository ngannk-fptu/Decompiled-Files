/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.core.annotation.AliasFor;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
public @interface ImportResource {
    @AliasFor(value="locations")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] locations() default {};

    public Class<? extends BeanDefinitionReader> reader() default BeanDefinitionReader.class;
}

