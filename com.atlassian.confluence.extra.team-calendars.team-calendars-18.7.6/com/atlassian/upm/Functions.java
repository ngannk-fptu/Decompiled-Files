/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.upm.api.util.Option
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.MapMaker
 */
package com.atlassian.upm;

import com.atlassian.upm.Pairs;
import com.atlassian.upm.api.util.Option;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public final class Functions {
    private static final Map<String, Function<?, ?>> functionCache = new MapMaker().expiration(10L, TimeUnit.SECONDS).makeMap();
    private static final Map<String, Function2<?, ?, ?>> function2Cache = new MapMaker().expiration(10L, TimeUnit.SECONDS).makeMap();

    public static <F1, F2, T> Function<F1, Function<F2, T>> curry(final Function2<F1, F2, T> fn) {
        return new Function<F1, Function<F2, T>>(){

            public Function<F2, T> apply(final @Nullable F1 f1) {
                return new Function<F2, T>(){

                    public T apply(@Nullable F2 f2) {
                        return fn.apply(f1, f2);
                    }
                };
            }
        };
    }

    public static <F1, F2, T> Function<Pairs.ImmutablePair<F1, F2>, T> anticurry(final Function2<F1, F2, T> fn) {
        return new Function<Pairs.ImmutablePair<F1, F2>, T>(){

            public T apply(@Nullable Pairs.ImmutablePair<F1, F2> from) {
                return fn.apply(from.getFirst(), from.getSecond());
            }
        };
    }

    public static <In1, In2, Out> Iterable<Out> transform2(In1 in1, Iterable<In2> in2, Function2<In1, In2, Out> fn2) {
        return Iterables.transform(in2, (Function)((Function)Functions.curry(fn2).apply(in1)));
    }

    public static <In1, In2, Out> Map<In1, Out> transformValues2(Map<In1, In2> in, Function2<In1, In2, Out> fn2) {
        ImmutableMap.Builder out = ImmutableMap.builder();
        for (Map.Entry<In1, In2> entry : in.entrySet()) {
            out.put(entry.getKey(), fn2.apply(entry.getKey(), entry.getValue()));
        }
        return out.build();
    }

    public static <F, T> Iterable<T> applyEach(Iterable<? extends Function<F, T>> fns, F from) {
        ImmutableList.Builder out = ImmutableList.builder();
        for (Function<F, T> fn : fns) {
            out.add(fn.apply(from));
        }
        return out.build();
    }

    public static <F, T> Function<F, T> virtual(final String fnName) {
        return new Function<F, T>(){

            public T apply(@Nullable F f) {
                try {
                    return f.getClass().getMethod(fnName, new Class[0]).invoke(f, new Object[0]);
                }
                catch (Exception e) {
                    return null;
                }
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
        private final Map<Pairs.ImmutablePair<F1, F2>, Option<T>> cache;

        private CachedFunction2(Function2<F1, F2, T> fn) {
            this.cache = new MapMaker().expiration(10L, TimeUnit.SECONDS).makeComputingMap(NotNullFunction.notNull(Functions.anticurry(fn)));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static <F1, F2, T> Function2<F1, F2, T> cache(String name, Function2<F1, F2, T> fn) {
            Map map = function2Cache;
            synchronized (map) {
                CachedFunction2<F1, F2, T> cached = (CachedFunction2<F1, F2, T>)function2Cache.get(name);
                if (cached == null) {
                    cached = new CachedFunction2<F1, F2, T>(fn);
                    function2Cache.put(name, cached);
                }
                return cached;
            }
        }

        @Override
        public T apply(@Nullable F1 from1, @Nullable F2 from2) {
            Option<T> result = this.cache.get(Pairs.ImmutablePair.pair(from1, from2));
            Iterator i$ = result.iterator();
            if (i$.hasNext()) {
                Object t = i$.next();
                return (T)t;
            }
            return null;
        }
    }

    public static final class CachedFunction<F, T>
    implements Function<F, T> {
        private final Map<F, Option<T>> cache;

        private CachedFunction(Function<F, T> fn) {
            this.cache = new MapMaker().expiration(10L, TimeUnit.SECONDS).makeComputingMap(NotNullFunction.notNull(fn));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static <F, T> Function<F, T> cache(String name, Function<F, T> fn) {
            Map map = functionCache;
            synchronized (map) {
                CachedFunction<F, T> cached = (CachedFunction<F, T>)functionCache.get(name);
                if (cached == null) {
                    cached = new CachedFunction<F, T>(fn);
                    functionCache.put(name, cached);
                }
                return cached;
            }
        }

        public T apply(@Nullable F f) {
            return (T)this.cache.get(f).get();
        }
    }

    public static final class NotNullFunction2<F1, F2, T>
    implements Function2<F1, F2, Option<T>> {
        private final Function2<F1, F2, T> fn;

        private NotNullFunction2(Function2<F1, F2, T> fn) {
            this.fn = fn;
        }

        public static <F1, F2, T> Function2<F1, F2, Option<T>> notNull(Function2<F1, F2, T> fn) {
            return new NotNullFunction2<F1, F2, T>(fn);
        }

        @Override
        public Option<T> apply(@Nullable F1 from1, @Nullable F2 from2) {
            return Option.option(this.fn.apply(from1, from2));
        }
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

        public Option<T> apply(@Nullable F from) {
            return Option.option((Object)this.fn.apply(from));
        }
    }

    public static interface Function2<F1, F2, T> {
        public T apply(@Nullable F1 var1, @Nullable F2 var2);
    }
}

