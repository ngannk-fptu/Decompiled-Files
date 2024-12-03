/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.function;

import java.util.function.Supplier;

public class Suppliers {
    public static <T> T get(Supplier<T> supplier) {
        return supplier == null ? null : (T)supplier.get();
    }
}

