/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AliasFor
 */
package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method={RequestMethod.GET})
public @interface GetMapping {
    @AliasFor(annotation=RequestMapping.class)
    public String name() default "";

    @AliasFor(annotation=RequestMapping.class)
    public String[] value() default {};

    @AliasFor(annotation=RequestMapping.class)
    public String[] path() default {};

    @AliasFor(annotation=RequestMapping.class)
    public String[] params() default {};

    @AliasFor(annotation=RequestMapping.class)
    public String[] headers() default {};

    @AliasFor(annotation=RequestMapping.class)
    public String[] consumes() default {};

    @AliasFor(annotation=RequestMapping.class)
    public String[] produces() default {};
}

