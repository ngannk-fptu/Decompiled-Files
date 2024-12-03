/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.provider.jaxb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface XmlHeader {
    public String value();
}

