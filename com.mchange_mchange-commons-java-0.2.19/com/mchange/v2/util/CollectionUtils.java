/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public final class CollectionUtils {
    public static final SortedSet EMPTY_SORTED_SET = Collections.unmodifiableSortedSet(new TreeSet());
    static final Class[] EMPTY_ARG_CLASSES = new Class[0];
    static final Object[] EMPTY_ARGS = new Object[0];
    static final Class[] COMPARATOR_ARG_CLASSES = new Class[]{Comparator.class};
    static final Class[] COLLECTION_ARG_CLASSES = new Class[]{Collection.class};
    static final Class[] SORTED_SET_ARG_CLASSES = new Class[]{SortedSet.class};
    static final Class[] MAP_ARG_CLASSES = new Class[]{Map.class};
    static final Class[] SORTED_MAP_ARG_CLASSES = new Class[]{SortedMap.class};
    static final Class STD_UNMODIFIABLE_COLLECTION_CL;
    static final Class STD_UNMODIFIABLE_SET_CL;
    static final Class STD_UNMODIFIABLE_LIST_CL;
    static final Class STD_UNMODIFIABLE_RA_LIST_CL;
    static final Class STD_UNMODIFIABLE_SORTED_SET_CL;
    static final Class STD_UNMODIFIABLE_MAP_CL;
    static final Class STD_UNMODIFIABLE_SORTED_MAP_CL;
    static final Class STD_SYNCHRONIZED_COLLECTION_CL;
    static final Class STD_SYNCHRONIZED_SET_CL;
    static final Class STD_SYNCHRONIZED_LIST_CL;
    static final Class STD_SYNCHRONIZED_RA_LIST_CL;
    static final Class STD_SYNCHRONIZED_SORTED_SET_CL;
    static final Class STD_SYNCHRONIZED_MAP_CL;
    static final Class STD_SYNCHRONIZED_SORTED_MAP_CL;
    static final Set UNMODIFIABLE_WRAPPERS;
    static final Set SYNCHRONIZED_WRAPPERS;
    static final Set ALL_COLLECTIONS_WRAPPERS;

    public static boolean isCollectionsWrapper(Class clazz) {
        return ALL_COLLECTIONS_WRAPPERS.contains(clazz);
    }

    public static boolean isCollectionsWrapper(Collection collection) {
        return CollectionUtils.isCollectionsWrapper(collection.getClass());
    }

    public static boolean isCollectionsWrapper(Map map) {
        return CollectionUtils.isCollectionsWrapper(map.getClass());
    }

    public static boolean isSynchronizedWrapper(Class clazz) {
        return SYNCHRONIZED_WRAPPERS.contains(clazz);
    }

    public static boolean isSynchronizedWrapper(Collection collection) {
        return CollectionUtils.isSynchronizedWrapper(collection.getClass());
    }

    public static boolean isSynchronizedWrapper(Map map) {
        return CollectionUtils.isSynchronizedWrapper(map.getClass());
    }

    public static boolean isUnmodifiableWrapper(Class clazz) {
        return UNMODIFIABLE_WRAPPERS.contains(clazz);
    }

    public static boolean isUnmodifiableWrapper(Collection collection) {
        return CollectionUtils.isUnmodifiableWrapper(collection.getClass());
    }

    public static boolean isUnmodifiableWrapper(Map map) {
        return CollectionUtils.isUnmodifiableWrapper(map.getClass());
    }

    public static Collection narrowUnmodifiableCollection(Collection collection) {
        if (collection instanceof SortedSet) {
            return Collections.unmodifiableSortedSet((SortedSet)collection);
        }
        if (collection instanceof Set) {
            return Collections.unmodifiableSet((Set)collection);
        }
        if (collection instanceof List) {
            return Collections.unmodifiableList((List)collection);
        }
        return Collections.unmodifiableCollection(collection);
    }

    public static Collection narrowSynchronizedCollection(Collection collection) {
        if (collection instanceof SortedSet) {
            return Collections.synchronizedSortedSet((SortedSet)collection);
        }
        if (collection instanceof Set) {
            return Collections.synchronizedSet((Set)collection);
        }
        if (collection instanceof List) {
            return Collections.synchronizedList((List)collection);
        }
        return Collections.synchronizedCollection(collection);
    }

    public static Map narrowUnmodifiableMap(Map map) {
        if (map instanceof SortedMap) {
            return Collections.unmodifiableSortedMap((SortedMap)map);
        }
        return Collections.unmodifiableMap(map);
    }

    public static Map narrowSynchronizedMap(Map map) {
        if (map instanceof SortedMap) {
            return Collections.synchronizedSortedMap((SortedMap)map);
        }
        return Collections.synchronizedMap(map);
    }

    public static Collection attemptClone(Collection collection) throws NoSuchMethodException {
        Executable executable;
        if (collection instanceof Vector) {
            return (Collection)((Vector)collection).clone();
        }
        if (collection instanceof ArrayList) {
            return (Collection)((ArrayList)collection).clone();
        }
        if (collection instanceof LinkedList) {
            return (Collection)((LinkedList)collection).clone();
        }
        if (collection instanceof HashSet) {
            return (Collection)((HashSet)collection).clone();
        }
        if (collection instanceof TreeSet) {
            return (Collection)((TreeSet)collection).clone();
        }
        Collection collection2 = null;
        Class<?> clazz = collection.getClass();
        try {
            executable = clazz.getMethod("clone", EMPTY_ARG_CLASSES);
            collection2 = (Collection)((Method)executable).invoke(collection, EMPTY_ARGS);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (collection2 == null) {
            try {
                executable = clazz.getConstructor(collection instanceof SortedSet ? SORTED_SET_ARG_CLASSES : COLLECTION_ARG_CLASSES);
                collection2 = (Collection)((Constructor)executable).newInstance(collection);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (collection2 == null) {
            try {
                executable = clazz.getConstructor(clazz);
                collection2 = (Collection)((Constructor)executable).newInstance(collection);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (collection2 == null) {
            throw new NoSuchMethodException("No accessible clone() method or reasonable copy constructor could be called on Collection " + collection);
        }
        return collection2;
    }

    public static Map attemptClone(Map map) throws NoSuchMethodException {
        Executable executable;
        if (map instanceof Properties) {
            return (Map)((Properties)map).clone();
        }
        if (map instanceof Hashtable) {
            return (Map)((Hashtable)map).clone();
        }
        if (map instanceof HashMap) {
            return (Map)((HashMap)map).clone();
        }
        if (map instanceof TreeMap) {
            return (Map)((TreeMap)map).clone();
        }
        Map map2 = null;
        Class<?> clazz = map.getClass();
        try {
            executable = clazz.getMethod("clone", EMPTY_ARG_CLASSES);
            map2 = (Map)((Method)executable).invoke(map, EMPTY_ARGS);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (map2 == null) {
            try {
                executable = clazz.getConstructor(map instanceof SortedMap ? SORTED_MAP_ARG_CLASSES : MAP_ARG_CLASSES);
                map2 = (Map)((Constructor)executable).newInstance(map);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (map2 == null) {
            try {
                executable = clazz.getConstructor(clazz);
                map2 = (Map)((Constructor)executable).newInstance(map);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (map2 == null) {
            throw new NoSuchMethodException("No accessible clone() method or reasonable copy constructor could be called on Map " + map);
        }
        return map2;
    }

    public static void add(Collection collection, Object object) {
        collection.add(object);
    }

    public static void remove(Collection collection, Object object) {
        collection.remove(object);
    }

    public static int size(Object object) {
        if (object instanceof Collection) {
            return ((Collection)object).size();
        }
        if (object instanceof Map) {
            return ((Map)object).size();
        }
        if (object instanceof Object[]) {
            return ((Object[])object).length;
        }
        if (object instanceof boolean[]) {
            return ((boolean[])object).length;
        }
        if (object instanceof byte[]) {
            return ((byte[])object).length;
        }
        if (object instanceof char[]) {
            return ((char[])object).length;
        }
        if (object instanceof short[]) {
            return ((short[])object).length;
        }
        if (object instanceof int[]) {
            return ((int[])object).length;
        }
        if (object instanceof long[]) {
            return ((long[])object).length;
        }
        if (object instanceof float[]) {
            return ((float[])object).length;
        }
        if (object instanceof double[]) {
            return ((double[])object).length;
        }
        throw new IllegalArgumentException(object + " must be a Collection, Map, or array!");
    }

    private CollectionUtils() {
    }

    static {
        HashSet hashSet = new HashSet();
        TreeSet treeSet = new TreeSet();
        LinkedList linkedList = new LinkedList();
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        TreeMap treeMap = new TreeMap();
        HashSet hashSet2 = new HashSet();
        HashSet hashSet3 = new HashSet();
        STD_UNMODIFIABLE_COLLECTION_CL = Collections.unmodifiableCollection(arrayList).getClass();
        hashSet2.add(STD_UNMODIFIABLE_COLLECTION_CL);
        STD_UNMODIFIABLE_SET_CL = Collections.unmodifiableSet(hashSet).getClass();
        hashSet2.add(STD_UNMODIFIABLE_SET_CL);
        STD_UNMODIFIABLE_LIST_CL = Collections.unmodifiableList(linkedList).getClass();
        hashSet2.add(STD_UNMODIFIABLE_LIST_CL);
        STD_UNMODIFIABLE_RA_LIST_CL = Collections.unmodifiableList(arrayList).getClass();
        hashSet2.add(STD_UNMODIFIABLE_RA_LIST_CL);
        STD_UNMODIFIABLE_SORTED_SET_CL = Collections.unmodifiableSortedSet(treeSet).getClass();
        hashSet2.add(STD_UNMODIFIABLE_SORTED_SET_CL);
        STD_UNMODIFIABLE_MAP_CL = Collections.unmodifiableMap(hashMap).getClass();
        hashSet2.add(STD_UNMODIFIABLE_MAP_CL);
        STD_UNMODIFIABLE_SORTED_MAP_CL = Collections.unmodifiableSortedMap(treeMap).getClass();
        hashSet2.add(STD_UNMODIFIABLE_SORTED_MAP_CL);
        STD_SYNCHRONIZED_COLLECTION_CL = Collections.synchronizedCollection(arrayList).getClass();
        hashSet3.add(STD_SYNCHRONIZED_COLLECTION_CL);
        STD_SYNCHRONIZED_SET_CL = Collections.synchronizedSet(hashSet).getClass();
        hashSet3.add(STD_SYNCHRONIZED_SET_CL);
        STD_SYNCHRONIZED_LIST_CL = Collections.synchronizedList(linkedList).getClass();
        hashSet3.add(STD_SYNCHRONIZED_LIST_CL);
        STD_SYNCHRONIZED_RA_LIST_CL = Collections.synchronizedList(arrayList).getClass();
        hashSet3.add(STD_SYNCHRONIZED_RA_LIST_CL);
        STD_SYNCHRONIZED_SORTED_SET_CL = Collections.synchronizedSortedSet(treeSet).getClass();
        hashSet3.add(STD_SYNCHRONIZED_SORTED_SET_CL);
        STD_SYNCHRONIZED_MAP_CL = Collections.synchronizedMap(hashMap).getClass();
        hashSet3.add(STD_SYNCHRONIZED_MAP_CL);
        STD_SYNCHRONIZED_SORTED_MAP_CL = Collections.synchronizedMap(treeMap).getClass();
        hashSet3.add(STD_SYNCHRONIZED_SORTED_MAP_CL);
        UNMODIFIABLE_WRAPPERS = Collections.unmodifiableSet(hashSet2);
        SYNCHRONIZED_WRAPPERS = Collections.unmodifiableSet(hashSet3);
        HashSet hashSet4 = new HashSet(hashSet2);
        hashSet4.addAll(hashSet3);
        ALL_COLLECTIONS_WRAPPERS = Collections.unmodifiableSet(hashSet4);
    }
}

