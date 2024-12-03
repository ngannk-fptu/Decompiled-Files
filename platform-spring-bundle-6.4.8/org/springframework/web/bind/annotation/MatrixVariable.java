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
public @interface MatrixVariable {
    @AliasFor(value="name")
    public String value() default "";

    @AliasFor(value="value")
    public String name() default "";

    public String pathVar() default "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n";

    public boolean required() default true;

    public String defaultValue() default "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n";
}

