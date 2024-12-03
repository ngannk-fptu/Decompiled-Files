/*
 * Decompiled with CFR 0.152.
 */
package edu.umd.cs.findbugs.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.CLASS)
public @interface SuppressFBWarnings {
    public String[] value() default {};

    public String justification() default "";
}

