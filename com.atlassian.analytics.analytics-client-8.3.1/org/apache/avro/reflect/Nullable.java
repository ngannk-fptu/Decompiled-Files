/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Nullable {
}

