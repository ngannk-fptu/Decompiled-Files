/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.Functions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

public abstract class Wrapper2<K, V, Out> {
    private final Functions.Function2<K, V, Out> wrapSingleton;
    private final Functions.Function2<K, V[], Iterable<Out>> wrapArray;
    private final Functions.Function2<K, Iterable<V>, Collection<Out>> wrapIterable;
    private final Function<Map<K, V>, Map<K, Out>> wrapSingletonValuedMap;
    private final Function<Map<K, V[]>, Map<K, Iterable<Out>>> wrapArrayValuedMap;
    private final Function<Map<K, Iterable<V>>, Map<K, Collection<Out>>> wrapIterableValuedMap;

    public Wrapper2(String name) {
        this.wrapSingleton = Functions.CachedFunction2.cache(name + ".wrapSingleton", (key, value) -> value == null ? null : this.wrap(key, value));
        this.wrapArray = Functions.CachedFunction2.cache(name + ".wrapArray", (key, values) -> values == null ? Collections.emptyList() : Functions.transform2(key, Arrays.asList(values), this.wrapSingleton));
        this.wrapIterable = Functions.CachedFunction2.cache(name + ".wrapIterable", (key, values) -> values == null ? Collections.emptyList() : Functions.transform2(key, values, this.wrapSingleton));
        this.wrapSingletonValuedMap = Functions.CachedFunction.cache(name + ".wrapSingletonValuedMap", in -> in == null ? Collections.emptyMap() : Functions.transformValues2(in, this.wrapSingleton));
        this.wrapArrayValuedMap = Functions.CachedFunction.cache(name + ".wrapArrayValuedMap", in -> in == null ? Collections.emptyMap() : Functions.transformValues2(in, this.wrapArray));
        this.wrapIterableValuedMap = Functions.CachedFunction.cache(name + ".wrapIterableValuedMap", in -> in == null ? Collections.emptyMap() : Functions.transformValues2(in, this.wrapIterable));
    }

    protected abstract Out wrap(@Nullable K var1, @Nullable V var2);

    public final Out fromSingleton(@Nullable K key, @Nullable V value) {
        return this.wrapSingleton.apply(key, value);
    }

    public final Iterable<Out> fromArray(@Nullable K key, @Nullable V[] values) {
        return this.wrapArray.apply(key, values);
    }

    public final Collection<Out> fromIterable(@Nullable K key, @Nullable Iterable<V> values) {
        return this.wrapIterable.apply(key, values);
    }

    public final Map<K, Out> fromSingletonValuedMap(@Nullable Map<K, V> in) {
        return this.wrapSingletonValuedMap.apply(in);
    }

    public final Map<K, Iterable<Out>> fromArrayValuedMap(@Nullable Map<K, V[]> in) {
        return this.wrapArrayValuedMap.apply(in);
    }

    public final Map<K, Collection<Out>> fromIterableValuedMap(@Nullable Map<K, Iterable<V>> in) {
        return this.wrapIterableValuedMap.apply(in);
    }
}

