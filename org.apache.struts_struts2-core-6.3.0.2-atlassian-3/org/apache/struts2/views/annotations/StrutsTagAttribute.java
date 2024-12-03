/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.views.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface StrutsTagAttribute {
    public String name() default "";

    public boolean required() default false;

    public boolean rtexprvalue() default false;

    public String description();

    public String defaultValue() default "";

    public String type() default "String";
}

