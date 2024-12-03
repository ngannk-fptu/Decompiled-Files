/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Create {
    public static <K, V> Map<K, V> map() {
        return new LinkedHashMap();
    }

    public static <K, V> Map<K, V> map(Class<K> key, Class<V> value) {
        return Collections.checkedMap(new LinkedHashMap(), key, value);
    }

    public static <T> List<T> list() {
        return new ArrayList();
    }

    public static <T> List<T> list(Class<T> c) {
        return Collections.checkedList(new ArrayList(), c);
    }

    public static <T> Set<T> set() {
        return new LinkedHashSet();
    }

    public static <T> Set<T> set(Class<T> c) {
        return Collections.checkedSet(new LinkedHashSet(), c);
    }

    @SafeVarargs
    public static <T> List<T> list(T ... source) {
        return new ArrayList<T>(Arrays.asList(source));
    }

    @SafeVarargs
    public static <T> Set<T> set(T ... source) {
        return new LinkedHashSet<T>(Arrays.asList(source));
    }

    public static <K, V> Map<K, V> copy(Map<K, V> source) {
        return new LinkedHashMap<K, V>(source);
    }

    public static <T> List<T> copy(List<T> source) {
        return new ArrayList<T>(source);
    }

    public static <T> Set<T> copy(Collection<T> source) {
        if (source == null) {
            return Create.set();
        }
        return new LinkedHashSet<T>(source);
    }
}

