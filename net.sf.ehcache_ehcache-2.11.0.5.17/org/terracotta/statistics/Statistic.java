/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface Statistic {
    public String name();

    public String[] tags() default {};
}

