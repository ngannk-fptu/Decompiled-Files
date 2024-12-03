/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion.annotations;

import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Conversion {
    public TypeConversion[] conversions() default {};
}

