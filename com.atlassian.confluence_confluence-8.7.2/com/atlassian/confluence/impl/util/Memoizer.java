/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.impl.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Memoizer<T, R> {
    private final Map<T, R> cache = new ConcurrentHashMap<T, R>();

    private Memoizer() {
    }

    private Function<T, R> doMemoize(Function<T, R> function) {
        return input -> this.cache.computeIfAbsent(input, function::apply);
    }

    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        return new Memoizer<T, R>().doMemoize(function);
    }

    public static <T, U, R> BiFunction<T, U, R> memoize(BiFunction<T, U, R> function) {
        Function<Object, Function> f = Memoizer.memoize((T t) -> Memoizer.memoize((T u) -> function.apply(t, u)));
        return (t, u) -> ((Function)f.apply(t)).apply(u);
    }
}

