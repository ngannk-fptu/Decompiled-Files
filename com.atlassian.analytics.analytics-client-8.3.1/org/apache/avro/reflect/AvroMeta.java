/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.FIELD})
@Repeatable(value=AvroMetas.class)
public @interface AvroMeta {
    public String key();

    public String value();

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE, ElementType.FIELD})
    public static @interface AvroMetas {
        public AvroMeta[] value();
    }
}

