/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class MapUtils {
    private MapUtils() {
    }

    public static <K, V> Map<K, V> of(K k1, V v1) {
        HashMap<K, V> m = new HashMap<K, V>();
        m.put(k1, v1);
        return m;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        HashMap<K, V> m = new HashMap<K, V>();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        HashMap<K, V> m = new HashMap<K, V>();
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        return m;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        HashMap<K, V> m = new HashMap<K, V>();
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        return m;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        HashMap<K, V> m = new HashMap<K, V>();
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        m.put(k5, v5);
        return m;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        HashMap<K, V> m = new HashMap<K, V>();
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        m.put(k4, v4);
        m.put(k5, v5);
        m.put(k6, v6);
        return m;
    }
}

