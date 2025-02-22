/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.dataflow.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Pure {

    public static enum Kind {
        SIDE_EFFECT_FREE,
        DETERMINISTIC;

    }
}

