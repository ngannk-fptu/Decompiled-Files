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
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {
    public String name() default "";

    @AliasFor(value="path")
    public String[] value() default {};

    @AliasFor(value="value")
    public String[] path() default {};

    public RequestMethod[] method() default {};

    public String[] params() default {};

    public String[] headers() default {};

    public String[] consumes() default {};

    public String[] produces() default {};
}

