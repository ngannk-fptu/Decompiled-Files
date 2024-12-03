/*
 * Decompiled with CFR 0.152.
 */
package com.google.j2objc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.CLASS)
public @interface ObjectiveCName {
    public String value();
}

