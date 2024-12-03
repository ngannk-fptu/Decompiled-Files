/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.UnmodifiableMapOfLists;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
        LinkedList<T> merged = new LinkedList<T>();
        if (list1 != null) {
            merged.addAll(list1);
        }
        if (list2 != null) {
            merged.addAll(list2);
        }
        return merged;
    }

    public static <T> T firstIfPresent(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static <T, U> Map<T, List<U>> deepCopyMap(Map<T, ? extends List<U>> map) {
        return CollectionUtils.deepCopyMap(map, () -> new LinkedHashMap(map.size()));
    }

    public static <T, U> Map<T, List<U>> deepCopyMap(Map<T, ? extends List<U>> map, Supplier<Map<T, List<U>>> mapConstructor) {
        Map result = mapConstructor.get();
        map.forEach((k, v) -> {
            List cfr_ignored_0 = result.put(k, new ArrayList(v));
        });
        return result;
    }

    public static <T, U> Map<T, List<U>> unmodifiableMapOfLists(Map<T, List<U>> map) {
        return new UnmodifiableMapOfLists<T, U>(map);
    }

    public static <T, U> Map<T, List<U>> deepUnmodifiableMap(Map<T, ? extends List<U>> map) {
        return CollectionUtils.unmodifiableMapOfLists(CollectionUtils.deepCopyMap(map));
    }

    public static <T, U> Map<T, List<U>> deepUnmodifiableMap(Map<T, ? extends List<U>> map, Supplier<Map<T, List<U>>> mapConstructor) {
        return CollectionUtils.unmodifiableMapOfLists(CollectionUtils.deepCopyMap(map, mapConstructor));
    }

    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, VInT, VOutT> Map<K, VOutT> mapValues(Map<K, VInT> inputMap, Function<VInT, VOutT> mapper) {
        return inputMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> mapper.apply(e.getValue())));
    }

    public static <K, V> Map<K, V> filterMap(Map<K, V> map, Predicate<Map.Entry<K, V>> condition) {
        return map.entrySet().stream().filter(condition).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> inverseMap(Map<V, K> inputMap) {
        return inputMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public static <K, V> Map<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> indexFunction) {
        HashMap<K, V> map = new HashMap<K, V>();
        for (V value : values) {
            K index = indexFunction.apply(value);
            V prev = map.put(index, value);
            Validate.isNull(prev, "No duplicate indices allowed but both %s and %s have the same index: %s", prev, value, index);
        }
        return map;
    }
}

