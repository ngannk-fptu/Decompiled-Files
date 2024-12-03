/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public class SingleUseIterable<T>
implements Iterable<T> {
    private final AtomicReference<Iterator<T>> holder;

    public static <T> @NonNull SingleUseIterable<T> create(Iterator<T> iterator) {
        return new SingleUseIterable<T>(iterator);
    }

    private SingleUseIterable(Iterator<T> iterator) {
        Objects.requireNonNull(iterator);
        this.holder = new AtomicReference<Iterator<T>>(iterator);
    }

    @Override
    public @NonNull Iterator<T> iterator() {
        Iterator it = this.holder.getAndSet(null);
        if (it == null) {
            throw new UnsupportedOperationException("This iterable can only be used once");
        }
        return it;
    }
}

