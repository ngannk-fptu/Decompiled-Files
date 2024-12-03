/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.cglib.core.$Predicate;
import com.google.inject.internal.cglib.core.$Transformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class $CollectionUtils {
    private $CollectionUtils() {
    }

    public static Map bucket(Collection c, $Transformer t) {
        HashMap buckets = new HashMap();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            Object key = t.transform(value);
            LinkedList bucket = (LinkedList)buckets.get(key);
            if (bucket == null) {
                bucket = new LinkedList();
                buckets.put(key, bucket);
            }
            bucket.add(value);
        }
        return buckets;
    }

    public static void reverse(Map source, Map target) {
        Iterator it = source.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            target.put(source.get(key), key);
        }
    }

    public static Collection filter(Collection c, $Predicate p) {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            if (p.evaluate(it.next())) continue;
            it.remove();
        }
        return c;
    }

    public static List transform(Collection c, $Transformer t) {
        ArrayList<Object> result = new ArrayList<Object>(c.size());
        Iterator it = c.iterator();
        while (it.hasNext()) {
            result.add(t.transform(it.next()));
        }
        return result;
    }

    public static Map getIndexMap(List list) {
        HashMap indexes = new HashMap();
        int index = 0;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            indexes.put(it.next(), new Integer(index++));
        }
        return indexes;
    }
}

