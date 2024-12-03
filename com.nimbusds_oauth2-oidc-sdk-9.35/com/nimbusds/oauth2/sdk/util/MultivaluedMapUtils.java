/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MultivaluedMapUtils {
    public static <K, V> Map<K, V> toSingleValuedMap(Map<K, List<V>> map) {
        if (map == null) {
            return null;
        }
        HashMap<K, V> out = new HashMap<K, V>();
        for (Map.Entry<K, List<V>> en : map.entrySet()) {
            if (en.getValue() == null || en.getValue().isEmpty()) {
                out.put(en.getKey(), null);
                continue;
            }
            out.put(en.getKey(), en.getValue().get(0));
        }
        return out;
    }

    public static <K, V> V getFirstValue(Map<K, List<V>> map, K key) {
        List<V> valueList = map.get(key);
        if (valueList == null || valueList.isEmpty()) {
            return null;
        }
        return valueList.get(0);
    }

    public static <K, V> V removeAndReturnFirstValue(Map<K, List<V>> map, String key) {
        List<V> valueList = map.remove(key);
        if (valueList == null || valueList.isEmpty()) {
            return null;
        }
        return valueList.get(0);
    }

    public static <K, V> Set<K> getKeysWithMoreThanOneValue(Map<K, List<V>> map, Set<K> excepted) {
        if (map == null || map.isEmpty()) {
            return Collections.emptySet();
        }
        HashSet<K> found = new HashSet<K>();
        for (Map.Entry<K, List<V>> en : map.entrySet()) {
            if (CollectionUtils.contains(excepted, en.getKey()) || en.getValue() == null || en.getValue().size() <= 1) continue;
            found.add(en.getKey());
        }
        return found;
    }

    private MultivaluedMapUtils() {
    }
}

