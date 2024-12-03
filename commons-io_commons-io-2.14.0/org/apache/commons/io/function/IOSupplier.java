/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.function;

import java.io.IOException;
import java.util.function.Supplier;
import org.apache.commons.io.function.Uncheck;

@FunctionalInterface
public interface IOSupplier<T> {
    default public Supplier<T> asSupplier() {
        return () -> Uncheck.get(this);
    }

    public T get() throws IOException;
}

