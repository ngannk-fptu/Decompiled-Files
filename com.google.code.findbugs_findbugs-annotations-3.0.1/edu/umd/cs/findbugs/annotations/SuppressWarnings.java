/*
 * Decompiled with CFR 0.152.
 */
package edu.umd.cs.findbugs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.CLASS)
@Deprecated
public @interface SuppressWarnings {
    public String[] value() default {};

    public String justification() default "";
}

