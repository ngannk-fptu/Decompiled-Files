/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.util;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Meter;

public final class MeterEquivalence {
    private MeterEquivalence() {
    }

    public static boolean equals(@Nullable Meter m1, @Nullable Object o) {
        if (m1 == null && o != null) {
            return false;
        }
        if (o == null && m1 != null) {
            return false;
        }
        if (!(o instanceof Meter)) {
            return false;
        }
        if (m1 == o) {
            return true;
        }
        Meter m2 = (Meter)o;
        return m1.getId().equals(m2.getId());
    }

    public static int hashCode(Meter m) {
        return m.getId().hashCode();
    }
}

