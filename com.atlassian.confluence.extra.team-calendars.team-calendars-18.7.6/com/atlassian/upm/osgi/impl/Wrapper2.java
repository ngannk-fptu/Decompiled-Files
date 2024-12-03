/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.Functions;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;

public abstract class Wrapper2<K, V, Out> {
    private final Functions.Function2<K, V, Out> wrapSingleton;
    private final Functions.Function2<K, V[], Iterable<Out>> wrapArray;
    private final Functions.Function2<K, Iterable<V>, Collection<Out>> wrapIterable;
    private final Function<Map<K, V>, Map<K, Out>> wrapSingletonValuedMap;
    private final Function<Map<K, V[]>, Map<K, Iterable<Out>>> wrapArrayValuedMap;
    private final Function<Map<K, Iterable<V>>, Map<K, Collection<Out>>> wrapIterableValuedMap;

    public Wrapper2(String name) {
        this.wrapSingleton = Functions.CachedFunction2.cache(name + ".wrapSingleton", new Functions.Function2<K, V, Out>(){

            @Override
            public Out apply(@Nullable K key, @Nullable V value) {
                return value == null ? null : (Object)Wrapper2.this.wrap(key, value);
            }
        });
        this.wrapArray = Functions.CachedFunction2.cache(name + ".wrapArray", new Functions.Function2<K, V[], Iterable<Out>>(){

            @Override
            public Iterable<Out> apply(@Nullable K key, @Nullable V[] values) {
                return values == null ? Collections.emptyList() : ImmutableList.copyOf(Functions.transform2(key, Arrays.asList(values), Wrapper2.this.wrapSingleton));
            }
        });
        this.wrapIterable = Functions.CachedFunction2.cache(name + ".wrapIterable", new Functions.Function2<K, Iterable<V>, Collection<Out>>(){

            @Override
            public Collection<Out> apply(@Nullable K key, @Nullable Iterable<V> values) {
                return values == null ? Collections.emptyList() : ImmutableList.copyOf(Functions.transform2(key, values, Wrapper2.this.wrapSingleton));
            }
        });
        this.wrapSingletonValuedMap = Functions.CachedFunction.cache(name + ".wrapSingletonValuedMap", new Function<Map<K, V>, Map<K, Out>>(){

            public Map<K, Out> apply(@Nullable Map<K, V> in) {
                return in == null ? Collections.emptyMap() : ImmutableMap.copyOf(Functions.transformValues2(in, Wrapper2.this.wrapSingleton));
            }
        });
        this.wrapArrayValuedMap = Functions.CachedFunction.cache(name + ".wrapArrayValuedMap", new Function<Map<K, V[]>, Map<K, Iterable<Out>>>(){

            public Map<K, Iterable<Out>> apply(@Nullable Map<K, V[]> in) {
                return in == null ? Collections.emptyMap() : ImmutableMap.copyOf(Functions.transformValues2(in, Wrapper2.this.wrapArray));
            }
        });
        this.wrapIterableValuedMap = Functions.CachedFunction.cache(name + ".wrapIterableValuedMap", new Function<Map<K, Iterable<V>>, Map<K, Collection<Out>>>(){

            public Map<K, Collection<Out>> apply(@Nullable Map<K, Iterable<V>> in) {
                return in == null ? Collections.emptyMap() : ImmutableMap.copyOf(Functions.transformValues2(in, Wrapper2.this.wrapIterable));
            }
        });
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
        return (Map)this.wrapSingletonValuedMap.apply(in);
    }

    public final Map<K, Iterable<Out>> fromArrayValuedMap(@Nullable Map<K, V[]> in) {
        return (Map)this.wrapArrayValuedMap.apply(in);
    }

    public final Map<K, Collection<Out>> fromIterableValuedMap(@Nullable Map<K, Iterable<V>> in) {
        return (Map)this.wrapIterableValuedMap.apply(in);
    }
}

