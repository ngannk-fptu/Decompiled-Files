/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.data;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;

public abstract class SoyAbstractCachingValueProvider
implements SoyValueProvider {
    private volatile SoyValue resolvedValue = null;

    @Override
    public final SoyValue resolve() {
        SoyValue localResolvedValue = this.resolvedValue;
        if (localResolvedValue == null) {
            this.resolvedValue = localResolvedValue = this.compute();
        }
        return localResolvedValue;
    }

    @Override
    public boolean equals(SoyValueProvider other) {
        return this == other || other != null && this.resolve().equals(other.resolve());
    }

    public boolean equals(Object other) {
        if (other instanceof SoyValueProvider) {
            return this.equals((SoyValueProvider)other);
        }
        return false;
    }

    public int hashCode() {
        throw new UnsupportedOperationException("SoyAbstractCachingValueProvider is unsuitable for use as a hash key.");
    }

    protected abstract SoyValue compute();
}

