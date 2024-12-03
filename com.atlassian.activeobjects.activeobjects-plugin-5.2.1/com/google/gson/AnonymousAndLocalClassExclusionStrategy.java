/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class AnonymousAndLocalClassExclusionStrategy
implements ExclusionStrategy {
    AnonymousAndLocalClassExclusionStrategy() {
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return this.isAnonymousOrLocal(f.getDeclaredClass());
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return this.isAnonymousOrLocal(clazz);
    }

    private boolean isAnonymousOrLocal(Class<?> clazz) {
        return !Enum.class.isAssignableFrom(clazz) && (clazz.isAnonymousClass() || clazz.isLocalClass());
    }
}

