/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueAnnotations {
    private static final AtomicInteger nextUniqueValue = new AtomicInteger(1);

    private UniqueAnnotations() {
    }

    public static Annotation create() {
        return UniqueAnnotations.create(nextUniqueValue.getAndIncrement());
    }

    static Annotation create(final int value) {
        return new Internal(){

            @Override
            public int value() {
                return value;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Internal.class;
            }

            @Override
            public String toString() {
                return "@" + Internal.class.getName() + "(value=" + value + ")";
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof Internal && ((Internal)o).value() == this.value();
            }

            @Override
            public int hashCode() {
                return 127 * "value".hashCode() ^ value;
            }
        };
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @BindingAnnotation
    static @interface Internal {
        public int value();
    }
}

