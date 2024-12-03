/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.CLASS)
@Documented
@Deprecated
public @interface FormatWith {
    public Class<?> value();
}

