/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.Path;

final class MemoKey {
    private final AbstractConfigValue value;
    private final Path restrictToChildOrNull;

    MemoKey(AbstractConfigValue value, Path restrictToChildOrNull) {
        this.value = value;
        this.restrictToChildOrNull = restrictToChildOrNull;
    }

    public final int hashCode() {
        int h = System.identityHashCode(this.value);
        if (this.restrictToChildOrNull != null) {
            return h + 41 * (41 + this.restrictToChildOrNull.hashCode());
        }
        return h;
    }

    public final boolean equals(Object other) {
        if (other instanceof MemoKey) {
            MemoKey o = (MemoKey)other;
            if (o.value != this.value) {
                return false;
            }
            if (o.restrictToChildOrNull == this.restrictToChildOrNull) {
                return true;
            }
            if (o.restrictToChildOrNull == null || this.restrictToChildOrNull == null) {
                return false;
            }
            return o.restrictToChildOrNull.equals(this.restrictToChildOrNull);
        }
        return false;
    }

    public final String toString() {
        return "MemoKey(" + this.value + "@" + System.identityHashCode(this.value) + "," + this.restrictToChildOrNull + ")";
    }
}

