/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util.function;

import java.util.function.Supplier;
import org.springframework.lang.Nullable;

public abstract class SupplierUtils {
    @Nullable
    public static <T> T resolve(@Nullable Supplier<T> supplier) {
        return supplier != null ? (T)supplier.get() : null;
    }
}

