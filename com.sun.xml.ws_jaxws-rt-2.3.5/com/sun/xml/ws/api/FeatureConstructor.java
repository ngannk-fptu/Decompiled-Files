/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.CONSTRUCTOR})
public @interface FeatureConstructor {
    public String[] value() default {};
}

