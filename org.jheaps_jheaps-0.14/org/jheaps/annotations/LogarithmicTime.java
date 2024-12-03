/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface LogarithmicTime {
    public boolean amortized() default false;
}

