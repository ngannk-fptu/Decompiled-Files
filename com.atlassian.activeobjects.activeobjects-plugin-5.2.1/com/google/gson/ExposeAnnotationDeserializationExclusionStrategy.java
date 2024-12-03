/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ExposeAnnotationDeserializationExclusionStrategy
implements ExclusionStrategy {
    ExposeAnnotationDeserializationExclusionStrategy() {
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        Expose annotation = f.getAnnotation(Expose.class);
        if (annotation == null) {
            return true;
        }
        return !annotation.deserialize();
    }
}

