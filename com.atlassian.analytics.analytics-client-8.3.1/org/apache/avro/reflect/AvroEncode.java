/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.avro.reflect.CustomEncoding;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface AvroEncode {
    public Class<? extends CustomEncoding<?>> using();
}

