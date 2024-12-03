/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  io.atlassian.fugue.Functions
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Options
 */
package io.atlassian.fugue.extras;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ImmutableMaps {
    private ImmutableMaps() {
    }

    public static <K, V> BiFunction<K, V, Map.Entry<K, V>> mapEntry() {
        return Maps::immutableEntry;
    }

    public static <K, V> ImmutableMap<K, V> toMap(Iterable<Map.Entry<K, V>> from) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (Map.Entry<K, V> entry : from) {
            if (entry == null) continue;
            K key = entry.getKey();
            V value = entry.getValue();
            if (key == null || value == null) continue;
            mapBuilder.put(key, value);
        }
        return mapBuilder.build();
    }

    public static <T, K, V> ImmutableMap<K, V> toMap(Iterable<T> from, Function<? super T, ? extends K> keyTransformer, Function<? super T, ? extends V> valueTransformer) {
        return ImmutableMaps.toMap(com.google.common.collect.Iterables.transform(from, entry -> Maps.immutableEntry(keyTransformer.apply(entry), valueTransformer.apply(entry))));
    }

    public static <K, V> ImmutableMap<K, V> mapBy(Iterable<V> from, Function<? super V, ? extends K> keyTransformer) {
        return ImmutableMaps.toMap(from, keyTransformer, Functions.identity());
    }

    public static <K, V> ImmutableMap<K, V> mapTo(Iterable<K> from, Function<? super K, ? extends V> valueTransformer) {
        return ImmutableMaps.toMap(from, Functions.identity(), valueTransformer);
    }

    public static <K1, K2, V1, V2> ImmutableMap<K2, V2> transform(Map<K1, V1> from, Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>> function) {
        return ImmutableMaps.toMap(com.google.common.collect.Iterables.transform(from.entrySet(), function::apply));
    }

    public static <K1, K2, V1, V2> ImmutableMap<K2, V2> transform(Map<K1, V1> from, Function<? super K1, ? extends K2> keyTransformer, Function<? super V1, ? extends V2> valueTransformer) {
        return ImmutableMaps.toMap(com.google.common.collect.Iterables.transform(from.entrySet(), entry -> Maps.immutableEntry(keyTransformer.apply((Object)entry.getKey()), valueTransformer.apply((Object)entry.getValue()))));
    }

    public static <K1, K2, V> ImmutableMap<K2, V> transformKey(Map<K1, V> from, Function<? super K1, ? extends K2> keyTransformer) {
        return ImmutableMaps.transform(from, keyTransformer, Functions.identity());
    }

    public static <K, V1, V2> ImmutableMap<K, V2> transformValue(Map<K, V1> from, Function<? super V1, ? extends V2> valueTransformer) {
        return ImmutableMaps.transform(from, Functions.identity(), valueTransformer);
    }

    public static <K1, K2, V1, V2> ImmutableMap<K2, V2> collect(Map<K1, V1> from, Function<Map.Entry<K1, V1>, Option<Map.Entry<K2, V2>>> partial) {
        return ImmutableMaps.toMap(Iterables.collect(from.entrySet(), partial));
    }

    public static <K1, K2, V1, V2> ImmutableMap<K2, V2> collect(Map<K1, V1> from, Function<? super K1, Option<K2>> keyPartial, Function<? super V1, Option<V2>> valuePartial) {
        return ImmutableMaps.collect(from, input -> {
            Option ok = (Option)keyPartial.apply((Object)input.getKey());
            Option ov = (Option)valuePartial.apply((Object)input.getValue());
            return (Option)Options.lift2(ImmutableMaps.mapEntry()).apply(ok, ov);
        });
    }

    public static <K1, K2, V> ImmutableMap<K2, V> collectByKey(Map<K1, V> from, Function<? super K1, Option<K2>> keyPartial) {
        return ImmutableMaps.collect(from, keyPartial, Options.toOption());
    }

    public static <K, V1, V2> ImmutableMap<K, V2> collectByValue(Map<K, V1> from, Function<? super V1, Option<V2>> valuePartial) {
        return ImmutableMaps.collect(from, Options.toOption(), valuePartial);
    }
}

