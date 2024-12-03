/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.internal.core.service.FactoryUtils;
import com.atlassian.vcache.internal.core.service.VCacheLock;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LocalCacheUtils {
    public static <K, V> Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, Iterable<K> keys, Function<K, Optional<V>> getFn, Function<PutArgs<K, V>, Optional<V>> putIfAbsentFn, VCacheLock lock) {
        Map existingValues = lock.withLock(() -> StreamSupport.stream(keys.spliterator(), false).distinct().collect(Collectors.toMap(Objects::requireNonNull, getFn)));
        Map<Object, Object> grandResult = existingValues.entrySet().stream().filter(e -> ((Optional)e.getValue()).isPresent()).collect(Collectors.toMap(Map.Entry::getKey, e -> ((Optional)e.getValue()).get()));
        if (grandResult.size() == existingValues.size()) {
            return grandResult;
        }
        Set missingKeys = existingValues.entrySet().stream().filter(e -> !((Optional)e.getValue()).isPresent()).map(Map.Entry::getKey).collect(Collectors.toSet());
        Map missingValues = factory.apply(missingKeys);
        FactoryUtils.verifyFactoryResult(missingValues, missingKeys);
        lock.withLock(() -> missingValues.entrySet().forEach(e -> {
            Optional existing = (Optional)putIfAbsentFn.apply(new PutArgs(e.getKey(), e.getValue()));
            grandResult.put(e.getKey(), existing.orElse(e.getValue()));
        }));
        return grandResult;
    }

    public static class PutArgs<K, V> {
        public final K key;
        public final V value;

        private PutArgs(K key, V value) {
            this.key = Objects.requireNonNull(key);
            this.value = Objects.requireNonNull(value);
        }
    }
}

