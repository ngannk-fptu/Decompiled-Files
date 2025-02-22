/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.util.JacksonFeature;

public final class JacksonFeatureSet<F extends JacksonFeature> {
    protected int _enabled;

    protected JacksonFeatureSet(int bitmask) {
        this._enabled = bitmask;
    }

    public static <F extends JacksonFeature> JacksonFeatureSet<F> fromDefaults(F[] allFeatures) {
        if (allFeatures.length > 31) {
            String desc = allFeatures[0].getClass().getName();
            throw new IllegalArgumentException(String.format("Can not use type `%s` with JacksonFeatureSet: too many entries (%d > 31)", desc, allFeatures.length));
        }
        int flags = 0;
        for (F f : allFeatures) {
            if (!f.enabledByDefault()) continue;
            flags |= f.getMask();
        }
        return new JacksonFeatureSet<F>(flags);
    }

    public static <F extends JacksonFeature> JacksonFeatureSet<F> fromBitmask(int bitmask) {
        return new JacksonFeatureSet<F>(bitmask);
    }

    public JacksonFeatureSet<F> with(F feature) {
        int newMask = this._enabled | feature.getMask();
        return newMask == this._enabled ? this : new JacksonFeatureSet<F>(newMask);
    }

    public JacksonFeatureSet<F> without(F feature) {
        int newMask = this._enabled & ~feature.getMask();
        return newMask == this._enabled ? this : new JacksonFeatureSet<F>(newMask);
    }

    public boolean isEnabled(F feature) {
        return (feature.getMask() & this._enabled) != 0;
    }

    public int asBitmask() {
        return this._enabled;
    }
}

