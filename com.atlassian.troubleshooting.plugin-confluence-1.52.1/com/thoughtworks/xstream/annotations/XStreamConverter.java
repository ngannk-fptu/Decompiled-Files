/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.annotations;

import com.thoughtworks.xstream.converters.ConverterMatcher;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface XStreamConverter {
    public Class<? extends ConverterMatcher> value();

    public int priority() default 0;

    public boolean useImplicitType() default true;

    public Class<?>[] types() default {};

    public String[] strings() default {};

    public byte[] bytes() default {};

    public char[] chars() default {};

    public short[] shorts() default {};

    public int[] ints() default {};

    public long[] longs() default {};

    public float[] floats() default {};

    public double[] doubles() default {};

    public boolean[] booleans() default {};

    public Class<?>[] nulls() default {};
}

