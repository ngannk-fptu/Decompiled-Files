/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang;

public final class TVLUtils {
    public static final boolean isDefinitelyTrue(Boolean bl) {
        return bl != null && bl != false;
    }

    public static final boolean isDefinitelyFalse(Boolean bl) {
        return bl != null && bl == false;
    }

    public static final boolean isPossiblyTrue(Boolean bl) {
        return bl == null || bl != false;
    }

    public static final boolean isPossiblyFalse(Boolean bl) {
        return bl == null || bl == false;
    }

    public static final boolean isUnknown(Boolean bl) {
        return bl == null;
    }
}

