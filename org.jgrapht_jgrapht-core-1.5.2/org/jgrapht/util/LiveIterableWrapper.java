/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

public class LiveIterableWrapper<E>
implements Iterable<E> {
    private Supplier<Iterable<E>> supplier;

    public LiveIterableWrapper() {
        this(null);
    }

    public LiveIterableWrapper(Supplier<Iterable<E>> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public Iterator<E> iterator() {
        return this.supplier.get().iterator();
    }

    public Supplier<Iterable<E>> getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier<Iterable<E>> supplier) {
        this.supplier = supplier;
    }
}

