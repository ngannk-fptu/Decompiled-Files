/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.avro.reflect.AvroAliases;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.FIELD})
@Repeatable(value=AvroAliases.class)
public @interface AvroAlias {
    public static final String NULL = "NOT A VALID NAMESPACE";

    public String alias();

    public String space() default "NOT A VALID NAMESPACE";
}

