/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion.annotations;

import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.impl.XWorkBasicConverter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface TypeConversion {
    public String key() default "";

    public ConversionType type() default ConversionType.CLASS;

    public ConversionRule rule() default ConversionRule.PROPERTY;

    public String converter() default "";

    public Class<?> converterClass() default XWorkBasicConverter.class;

    public String value() default "";
}

