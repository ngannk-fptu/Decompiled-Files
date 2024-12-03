/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import java.util.Collection;
import java.util.HashSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ModifierBasedExclusionStrategy
implements ExclusionStrategy {
    private final Collection<Integer> modifiers = new HashSet<Integer>();

    public ModifierBasedExclusionStrategy(int ... modifiers) {
        if (modifiers != null) {
            for (int modifier : modifiers) {
                this.modifiers.add(modifier);
            }
        }
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        for (int modifier : this.modifiers) {
            if (!f.hasModifier(modifier)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}

