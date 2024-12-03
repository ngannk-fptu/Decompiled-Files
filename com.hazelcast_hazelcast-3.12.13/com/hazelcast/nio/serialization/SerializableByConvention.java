/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SerializableByConvention {
    public Reason value() default Reason.INHERITANCE;

    public static enum Reason {
        PUBLIC_API,
        INHERITANCE;

    }
}

