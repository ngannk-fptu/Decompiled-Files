/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.api.querydsl.stream;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.types.Expression;
import java.io.Closeable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface CloseableIterable<T>
extends Iterable<T>,
Closeable {
    @Override
    public CloseableIterator<T> iterator();

    public Optional<T> fetchFirst();

    public CloseableIterable<T> take(int var1);

    public CloseableIterable<T> takeWhile(Predicate<T> var1);

    public CloseableIterable<T> filter(Predicate<T> var1);

    public <D> CloseableIterable<D> map(Function<T, D> var1);

    public <D> CloseableIterable<D> map(Expression<D> var1);

    public <D> D foldLeft(D var1, BiFunction<D, T, D> var2);

    public void foreach(Consumer<T> var1);

    @Override
    public void close();
}

