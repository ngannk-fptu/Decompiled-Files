/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

class MultiValueMapCollector<T, K, V>
implements Collector<T, MultiValueMap<K, V>, MultiValueMap<K, V>> {
    private final Function<T, K> keyFunction;
    private final Function<T, V> valueFunction;

    private MultiValueMapCollector(Function<T, K> keyFunction, Function<T, V> valueFunction) {
        this.keyFunction = keyFunction;
        this.valueFunction = valueFunction;
    }

    static <T, K, V> MultiValueMapCollector<T, K, V> of(Function<T, K> keyFunction, Function<T, V> valueFunction) {
        return new MultiValueMapCollector<T, K, V>(keyFunction, valueFunction);
    }

    @Override
    public Supplier<MultiValueMap<K, V>> supplier() {
        return () -> CollectionUtils.toMultiValueMap(new HashMap());
    }

    @Override
    public BiConsumer<MultiValueMap<K, V>, T> accumulator() {
        return (map, t) -> map.add(this.keyFunction.apply(t), this.valueFunction.apply(t));
    }

    @Override
    public BinaryOperator<MultiValueMap<K, V>> combiner() {
        return (map1, map2) -> {
            for (Object key : map2.keySet()) {
                map1.addAll(key, (List)map2.get(key));
            }
            return map1;
        };
    }

    @Override
    public Function<MultiValueMap<K, V>, MultiValueMap<K, V>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return EnumSet.of(Collector.Characteristics.IDENTITY_FINISH, Collector.Characteristics.UNORDERED);
    }
}

