/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class SyntheticFieldExclusionStrategy
implements ExclusionStrategy {
    private final boolean skipSyntheticFields;

    SyntheticFieldExclusionStrategy(boolean skipSyntheticFields) {
        this.skipSyntheticFields = skipSyntheticFields;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return this.skipSyntheticFields && f.isSynthetic();
    }
}

