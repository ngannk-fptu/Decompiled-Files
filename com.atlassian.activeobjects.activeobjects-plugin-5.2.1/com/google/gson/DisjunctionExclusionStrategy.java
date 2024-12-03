/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.internal.$Gson$Preconditions;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DisjunctionExclusionStrategy
implements ExclusionStrategy {
    private final Collection<ExclusionStrategy> strategies;

    DisjunctionExclusionStrategy(Collection<ExclusionStrategy> strategies) {
        this.strategies = $Gson$Preconditions.checkNotNull(strategies);
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        for (ExclusionStrategy strategy : this.strategies) {
            if (!strategy.shouldSkipField(f)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        for (ExclusionStrategy strategy : this.strategies) {
            if (!strategy.shouldSkipClass(clazz)) continue;
            return true;
        }
        return false;
    }
}

