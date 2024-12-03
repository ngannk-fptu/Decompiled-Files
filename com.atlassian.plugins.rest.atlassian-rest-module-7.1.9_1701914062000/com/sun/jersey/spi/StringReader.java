/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface StringReader<T> {
    public T fromString(String var1);

    @Target(value={ElementType.TYPE})
    @Retention(value=RetentionPolicy.RUNTIME)
    @Documented
    public static @interface ValidateDefaultValue {
        public boolean value() default true;
    }
}

