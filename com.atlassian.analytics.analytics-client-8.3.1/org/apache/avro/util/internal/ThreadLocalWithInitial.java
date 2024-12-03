/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util.internal;

import java.util.function.Supplier;

public class ThreadLocalWithInitial {
    public static <T> ThreadLocal<T> of(Supplier<? extends T> supplier) {
        return ThreadLocal.withInitial(supplier);
    }
}

