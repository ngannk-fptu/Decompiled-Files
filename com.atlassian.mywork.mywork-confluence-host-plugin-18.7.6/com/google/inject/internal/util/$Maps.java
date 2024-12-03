/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$ImmutableEntry;
import com.google.inject.internal.util.$Nullable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Maps {
    private $Maps() {
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap();
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap();
    }

    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap();
    }

    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap();
    }

    public static <K, V> Map.Entry<K, V> immutableEntry(@$Nullable K key, @$Nullable V value) {
        return new $ImmutableEntry<K, V>(key, value);
    }
}

