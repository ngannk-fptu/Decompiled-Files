/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.troubleshooting.healthcheck.util;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

public class MapHelper {
    public static <K, V> Map<K, V> asMap(K key, V value, Object ... keysValues) {
        HashMap<Object, Object> res = new HashMap<Object, Object>();
        res.put(key, value);
        if (keysValues.length > 0) {
            if (keysValues.length % 2 > 0) {
                throw new IllegalArgumentException("Arguments count must be even!");
            }
            for (int i = 0; i < keysValues.length; i += 2) {
                Object k = keysValues[i];
                Object v = keysValues[i + 1];
                res.put(k, v);
            }
        }
        return ImmutableMap.copyOf(res);
    }
}

