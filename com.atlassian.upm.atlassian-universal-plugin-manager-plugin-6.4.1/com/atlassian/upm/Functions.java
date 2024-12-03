/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheException
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ComputationException
 *  com.google.common.util.concurrent.ExecutionError
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.upm;

import com.atlassian.cache.CacheException;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.Pairs;
import com.atlassian.upm.api.util.Option;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ComputationException;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Functions {
    private static final Cache<String, Function<?, ?>> functionCache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(10L)).build();
    private static final Cache<String, Function2<?, ?, ?>> function2Cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(10L)).build();

    public static <F1, F2, T> Function<F1, Function<F2, T>> curry(Function2<F1, F2, T> fn) {
        return f1 -> f2 -> fn.apply(f1, f2);
    }

    public static <F1, F2, T> Function<Pairs.ImmutablePair<F1, F2>, T> anticurry(Function2<F1, F2, T> fn) {
        return from -> fn.apply(from.getFirst(), from.getSecond());
    }

    public static <In1, In2, Out> List<Out> transform2(In1 in1, Iterable<In2> in2, Function2<In1, In2, Out> fn2) {
        return Iterables.toStream(in2).map(Functions.curry(fn2).apply(in1)).collect(Collectors.toList());
    }

    public static <In1, In2, Out> Map<In1, Out> transformValues2(Map<In1, In2> in, Function2<In1, In2, Out> fn2) {
        HashMap<In1, Out> out = new HashMap<In1, Out>();
        for (Map.Entry<In1, In2> entry : in.entrySet()) {
            out.put(entry.getKey(), fn2.apply(entry.getKey(), entry.getValue()));
        }
        return Collections.unmodifiableMap(out);
    }

    public static <F, T> List<T> applyEach(Iterable<? extends Function<F, T>> fns, F from) {
        ArrayList<T> out = new ArrayList<T>();
        for (Function<F, F> function : fns) {
            out.add(function.apply(from));
        }
        return Collections.unmodifiableList(out);
    }

    private static <T> T accessCache(CacheAccessor<T> accessor) {
        try {
            return accessor.get();
        }
        catch (ComputationException | UncheckedExecutionException | ExecutionException e) {
            throw new CacheException(e.getCause());
        }
        catch (ExecutionError e) {
            throw (Error)e.getCause();
        }
    }

    public static <T> Function<T, String> typedToString() {
        return Object::toString;
    }

    public static <F, T> Function<F, T> virtual(String fnName) {
        return f -> {
            try {
                return f.getClass().getMethod(fnName, new Class[0]).invoke(f, new Object[0]);
            }
            catch (Exception e) {
                return null;
            }
        };
    }

    public static <F, T> Function<F, T> getter(String propertyName) {
        StringBuilder getter = new StringBuilder();
        getter.append("get");
        boolean initial = true;
        for (char c : propertyName.toCharArray()) {
            if (c == ' ') {
                initial = true;
                continue;
            }
            getter.append(initial ? Character.toUpperCase(c) : c);
            initial = false;
        }
        return Functions.virtual(getter.toString());
    }

    public static final class CachedFunction2<F1, F2, T>
    implements Function2<F1, F2, T> {
        private final LoadingCache<Pairs.ImmutablePair<F1, F2>, Option<T>> cache;

        private CachedFunction2(final Function2<F1, F2, T> fn) {
            this.cache = CacheBuilder.newBuilder().build(new CacheLoader<Pairs.ImmutablePair<F1, F2>, Option<T>>(){

                @Nonnull
                public Option<T> load(@Nonnull Pairs.ImmutablePair<F1, F2> f) {
                    return NotNullFunction.notNull(Functions.anticurry(fn)).apply(f);
                }
            });
        }

        public static <F1, F2, T> Function2<F1, F2, T> cache(String name, Function2<F1, F2, T> fn) {
            return (Function2)Functions.accessCache(() -> (Function2)function2Cache.get((Object)name, () -> new CachedFunction2(fn)));
        }

        @Override
        public T apply(@Nullable F1 from1, @Nullable F2 from2) {
            return (T)((Option)Functions.accessCache(() -> (Option)this.cache.get(Pairs.ImmutablePair.pair(from1, from2)))).getOrElse(null);
        }
    }

    public static final class CachedFunction<F, T>
    implements Function<F, T> {
        private final LoadingCache<F, Option<T>> cache;

        private CachedFunction(final Function<F, T> fn) {
            this.cache = CacheBuilder.newBuilder().build(new CacheLoader<F, Option<T>>(){

                @Nonnull
                public Option<T> load(@Nonnull F f) {
                    return NotNullFunction.notNull(fn).apply(f);
                }
            });
        }

        public static <F, T> Function<F, T> cache(String name, Function<F, T> fn) {
            return (Function)Functions.accessCache(() -> (Function)functionCache.get((Object)name, () -> new CachedFunction(fn)));
        }

        @Override
        public T apply(@Nullable F f) {
            return (T)((Option)Functions.accessCache(() -> (Option)this.cache.get(f))).get();
        }
    }

    @FunctionalInterface
    private static interface CacheAccessor<T> {
        public T get() throws ExecutionException;
    }

    public static final class NotNullFunction<F, T>
    implements Function<F, Option<T>> {
        private final Function<F, T> fn;

        private NotNullFunction(Function<F, T> fn) {
            this.fn = fn;
        }

        public static <F, T> Function<F, Option<T>> notNull(Function<F, T> fn) {
            return new NotNullFunction<F, T>(fn);
        }

        @Override
        public Option<T> apply(@Nullable F from) {
            return Option.option(this.fn.apply(from));
        }
    }

    public static interface Function2<F1, F2, T> {
        public T apply(@Nullable F1 var1, @Nullable F2 var2);
    }
}

