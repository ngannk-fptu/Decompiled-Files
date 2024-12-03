/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface Incubating {
    public String since();
}

