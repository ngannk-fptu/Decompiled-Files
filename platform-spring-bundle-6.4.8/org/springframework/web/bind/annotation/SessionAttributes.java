/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SessionAttributes {
    @AliasFor(value="names")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] names() default {};

    public Class<?>[] types() default {};
}

