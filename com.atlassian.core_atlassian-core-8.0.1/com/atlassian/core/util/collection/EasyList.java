/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util.collection;

import com.atlassian.core.util.ObjectUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class EasyList {
    public static <T> List<T> buildNull() {
        List<T> list = EasyList.createList(1);
        list.add(null);
        return list;
    }

    public static <T> List<T> build(T[] array) {
        List<T> list = EasyList.createList(array.length);
        Collections.addAll(list, array);
        return list;
    }

    public static <T> List<T> buildNonNull(T[] array) {
        List list;
        if (array != null && array.length > 0) {
            list = EasyList.createList(array.length);
            for (T o : array) {
                if (!ObjectUtils.isNotEmpty(o)) continue;
                list.add(o);
            }
        } else {
            list = Collections.emptyList();
        }
        return list;
    }

    public static <T> List<T> buildNonNull(Collection<T> c) {
        List list = c != null && !c.isEmpty() ? c.stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList()) : Collections.emptyList();
        return list;
    }

    public static <T> List<T> buildNonNull(T o) {
        if (ObjectUtils.isNotEmpty(o)) {
            return EasyList.build(o);
        }
        return EasyList.build();
    }

    public static <T> List<T> build() {
        return Collections.emptyList();
    }

    public static <T> List<T> build(T o1) {
        List<T> list = EasyList.createList(1);
        list.add(o1);
        return list;
    }

    public static <A, B extends A> List<A> build(A o1, B o2) {
        List list = EasyList.createList(2);
        list.add(o1);
        list.add(o2);
        return list;
    }

    public static <A, B extends A, C extends A> List<A> build(A o1, B o2, C o3) {
        List list = EasyList.createList(3);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        return list;
    }

    public static <A, B extends A, C extends A, D extends A> List<A> build(A o1, B o2, C o3, D o4) {
        List list = EasyList.createList(4);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }

    public static <A, B extends A, C extends A, D extends A, E extends A> List<A> build(A o1, B o2, C o3, D o4, E ... others) {
        List list = EasyList.createList(5);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        Collections.addAll(list, others);
        return list;
    }

    public static <T> List<T> build(Collection<T> collection) {
        if (collection == null) {
            return null;
        }
        return new ArrayList<T>(collection);
    }

    public static <T> List<T> createList(int size) {
        return new ArrayList(size);
    }

    public static <T, U extends T, V extends T> List<T> mergeLists(List<T> a, List<U> b, List<V> c) {
        List<T> d = EasyList.createList(0);
        if (a != null) {
            d.addAll(a);
        }
        if (b != null) {
            d.addAll(b);
        }
        if (c != null) {
            d.addAll(c);
        }
        return d;
    }

    public static <T> List<List<T>> shallowSplit(List<T> list, int sublength) {
        int overflow = list.size() % sublength > 0 ? 1 : 0;
        ArrayList<List<T>> result = new ArrayList<List<T>>(list.size() / sublength + overflow);
        for (int i = 0; i < list.size(); i += sublength) {
            int endIndex = i + sublength > list.size() ? list.size() : i + sublength;
            result.add(list.subList(i, endIndex));
        }
        return result;
    }
}

