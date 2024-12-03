/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface ManagedAnnotation {
    public String value();

    public boolean operation() default false;
}

