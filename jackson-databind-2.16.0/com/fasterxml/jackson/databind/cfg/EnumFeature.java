/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.cfg.DatatypeFeature;

public enum EnumFeature implements DatatypeFeature
{
    READ_ENUM_KEYS_USING_INDEX(false),
    WRITE_ENUMS_TO_LOWERCASE(false);

    private static final int FEATURE_INDEX = 0;
    private final boolean _enabledByDefault;
    private final int _mask;

    private EnumFeature(boolean enabledByDefault) {
        this._enabledByDefault = enabledByDefault;
        this._mask = 1 << this.ordinal();
    }

    public boolean enabledByDefault() {
        return this._enabledByDefault;
    }

    public boolean enabledIn(int flags) {
        return (flags & this._mask) != 0;
    }

    public int getMask() {
        return this._mask;
    }

    @Override
    public int featureIndex() {
        return 0;
    }
}

