/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core;

import software.amazon.awssdk.thirdparty.jackson.core.util.JacksonFeature;

public enum StreamReadCapability implements JacksonFeature
{
    DUPLICATE_PROPERTIES(false),
    SCALARS_AS_OBJECTS(false),
    UNTYPED_SCALARS(false),
    EXACT_FLOATS(false);

    private final boolean _defaultState;
    private final int _mask;

    private StreamReadCapability(boolean defaultState) {
        this._defaultState = defaultState;
        this._mask = 1 << this.ordinal();
    }

    @Override
    public boolean enabledByDefault() {
        return this._defaultState;
    }

    @Override
    public boolean enabledIn(int flags) {
        return (flags & this._mask) != 0;
    }

    @Override
    public int getMask() {
        return this._mask;
    }
}

