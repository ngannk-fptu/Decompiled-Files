/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.PARAMETER})
@Documented
@Deprecated
public @interface Field {
    public String name() default "";
}

