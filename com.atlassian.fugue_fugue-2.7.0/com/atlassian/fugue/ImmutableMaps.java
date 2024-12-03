/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Options;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

public class ImmutableMaps {
    private ImmutableMaps() {
    }

    public static <K, V> Function2<K, V, Map.Entry<K, V>> mapEntry() {
        MapEntryFunction<Object, Object> result = MapEntryFunction.INSTANCE;
        return result;
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

    public static <T, K, V> ImmutableMap<K, V> toMap(Iterable<T> from, final Function<? super T, ? extends K> keyTransformer, final Function<? super T, ? extends V> valueTransformer) {
        return ImmutableMaps.toMap(com.google.common.collect.Iterables.transform(from, (Function)new Function<T, Map.Entry<K, V>>(){

            public Map.Entry<K, V> apply(T input) {
                Map.Entry entry = Maps.immutableEntry((Object)keyTransformer.apply(input), (Object)valueTransformer.apply(input));
                return entry;
            }
        }));
    }

    public static <K, V> ImmutableMap<K, V> mapBy(Iterable<V> from, Function<? super V, ? extends K> keyTransformer) {
        return ImmutableMaps.toMap(from, keyTransformer, Functions.identity());
    }

    public static <K, V> ImmutableMap<K, V> mapTo(Iterable<K> from, Function<? super K, ? extends V> valueTransformer) {
        return ImmutableMaps.toMap(from, Functions.identity(), valueTransformer);
    }

    public static <K1, K2, V1, V2> ImmutableMap<K2, V2> transform(Map<K1, V1> from, Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>> function) {
        return ImmutableMaps.toMap(com.google.common.collect.Iterables.transform(from.entrySet(), function));
    }

    public static <K1, K2, V1, V2> ImmutableMap<K2, V2> transform(Map<K1, V1> from, final Function<? super K1, ? extends K2> keyTransformer, final Function<? super V1, ? extends V2> valueTransformer) {
        return ImmutableMaps.toMap(com.google.common.collect.Iterables.transform(from.entrySet(), (Function)new Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>>(){

            public Map.Entry<K2, V2> apply(Map.Entry<K1, V1> input) {
                Map.Entry entry = Maps.immutableEntry((Object)keyTransformer.apply(input.getKey()), (Object)valueTransformer.apply(input.getValue()));
                return entry;
            }
        }));
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

    public static <K1, K2, V1, V2> ImmutableMap<K2, V2> collect(Map<K1, V1> from, final Function<? super K1, Option<K2>> keyPartial, final Function<? super V1, Option<V2>> valuePartial) {
        return ImmutableMaps.collect(from, new Function<Map.Entry<K1, V1>, Option<Map.Entry<K2, V2>>>(){

            public Option<Map.Entry<K2, V2>> apply(Map.Entry<K1, V1> input) {
                Option ok = (Option)keyPartial.apply(input.getKey());
                Option ov = (Option)valuePartial.apply(input.getValue());
                return Options.lift2(ImmutableMaps.mapEntry()).apply(ok, ov);
            }
        });
    }

    public static <K1, K2, V> ImmutableMap<K2, V> collectByKey(Map<K1, V> from, Function<? super K1, Option<K2>> keyPartial) {
        return ImmutableMaps.collect(from, keyPartial, Option.toOption());
    }

    public static <K, V1, V2> ImmutableMap<K, V2> collectByValue(Map<K, V1> from, Function<? super V1, Option<V2>> valuePartial) {
        return ImmutableMaps.collect(from, Option.toOption(), valuePartial);
    }

    private static class MapEntryFunction<K, V>
    implements Function2<K, V, Map.Entry<K, V>> {
        static final MapEntryFunction<Object, Object> INSTANCE = new MapEntryFunction();

        private MapEntryFunction() {
        }

        @Override
        public Map.Entry<K, V> apply(K k, V v) {
            return Maps.immutableEntry(k, v);
        }
    }
}

