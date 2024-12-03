/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import java.util.function.Supplier;

public class NullnessHelper {
    public static <T> T coalesce(T ... values) {
        if (values == null) {
            return null;
        }
        for (T value : values) {
            if (value == null) continue;
            if (String.class.isInstance(value)) {
                if (((String)value).isEmpty()) continue;
                return value;
            }
            return value;
        }
        return null;
    }

    public static <T> T coalesceSuppliedValues(Supplier<T> ... valueSuppliers) {
        if (valueSuppliers == null) {
            return null;
        }
        for (Supplier<T> valueSupplier : valueSuppliers) {
            T value = valueSupplier.get();
            if (value == null) continue;
            return value;
        }
        return null;
    }
}

