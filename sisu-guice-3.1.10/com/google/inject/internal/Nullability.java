/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import java.lang.annotation.Annotation;

public class Nullability {
    private Nullability() {
    }

    public static boolean allowsNull(Annotation[] annotations) {
        for (Annotation a : annotations) {
            Class<? extends Annotation> type = a.annotationType();
            if (!"Nullable".equals(type.getSimpleName())) continue;
            return true;
        }
        return false;
    }
}

