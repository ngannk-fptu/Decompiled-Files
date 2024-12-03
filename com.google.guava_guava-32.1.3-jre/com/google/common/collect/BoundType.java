/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public enum BoundType {
    OPEN(false),
    CLOSED(true);

    final boolean inclusive;

    private BoundType(boolean inclusive) {
        this.inclusive = inclusive;
    }

    static BoundType forBoolean(boolean inclusive) {
        return inclusive ? CLOSED : OPEN;
    }
}

