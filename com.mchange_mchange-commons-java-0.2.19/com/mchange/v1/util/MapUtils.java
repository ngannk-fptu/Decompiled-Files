/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import java.util.Map;

public final class MapUtils {
    public static boolean equivalentDisregardingSort(Map map, Map map2) {
        if (map.size() != map2.size()) {
            return false;
        }
        for (Object k : map.keySet()) {
            if (map.get(k).equals(map2.get(k))) continue;
            return false;
        }
        return true;
    }

    public static int hashContentsDisregardingSort(Map map) {
        int n = 0;
        for (Object k : map.keySet()) {
            Object v = map.get(k);
            n ^= k.hashCode() ^ v.hashCode();
        }
        return n;
    }
}

