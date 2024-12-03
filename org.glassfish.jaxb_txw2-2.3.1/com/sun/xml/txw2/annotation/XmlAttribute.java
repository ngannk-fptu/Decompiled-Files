/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface XmlAttribute {
    public String value() default "";

    public String ns() default "";
}

