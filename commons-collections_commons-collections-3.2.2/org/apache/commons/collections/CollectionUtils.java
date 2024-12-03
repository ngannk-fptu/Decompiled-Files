/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.BoundedCollection;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.collection.PredicatedCollection;
import org.apache.commons.collections.collection.SynchronizedCollection;
import org.apache.commons.collections.collection.TransformedCollection;
import org.apache.commons.collections.collection.TypedCollection;
import org.apache.commons.collections.collection.UnmodifiableBoundedCollection;
import org.apache.commons.collections.collection.UnmodifiableCollection;

public class CollectionUtils {
    private static Integer INTEGER_ONE = new Integer(1);
    public static final Collection EMPTY_COLLECTION = UnmodifiableCollection.decorate(new ArrayList());

    public static Collection union(Collection a, Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = CollectionUtils.getCardinalityMap(a);
        Map mapb = CollectionUtils.getCardinalityMap(b);
        HashSet elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            int m = Math.max(CollectionUtils.getFreq(obj, mapa), CollectionUtils.getFreq(obj, mapb));
            for (int i = 0; i < m; ++i) {
                list.add(obj);
            }
        }
        return list;
    }

    public static Collection intersection(Collection a, Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = CollectionUtils.getCardinalityMap(a);
        Map mapb = CollectionUtils.getCardinalityMap(b);
        HashSet elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            int m = Math.min(CollectionUtils.getFreq(obj, mapa), CollectionUtils.getFreq(obj, mapb));
            for (int i = 0; i < m; ++i) {
                list.add(obj);
            }
        }
        return list;
    }

    public static Collection disjunction(Collection a, Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = CollectionUtils.getCardinalityMap(a);
        Map mapb = CollectionUtils.getCardinalityMap(b);
        HashSet elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            int m = Math.max(CollectionUtils.getFreq(obj, mapa), CollectionUtils.getFreq(obj, mapb)) - Math.min(CollectionUtils.getFreq(obj, mapa), CollectionUtils.getFreq(obj, mapb));
            for (int i = 0; i < m; ++i) {
                list.add(obj);
            }
        }
        return list;
    }

    public static Collection subtract(Collection a, Collection b) {
        ArrayList list = new ArrayList(a);
        Iterator it = b.iterator();
        while (it.hasNext()) {
            list.remove(it.next());
        }
        return list;
    }

    public static boolean containsAny(Collection coll1, Collection coll2) {
        if (coll1.size() < coll2.size()) {
            Iterator it = coll1.iterator();
            while (it.hasNext()) {
                if (!coll2.contains(it.next())) continue;
                return true;
            }
        } else {
            Iterator it = coll2.iterator();
            while (it.hasNext()) {
                if (!coll1.contains(it.next())) continue;
                return true;
            }
        }
        return false;
    }

    public static Map getCardinalityMap(Collection coll) {
        HashMap count = new HashMap();
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            Integer c = (Integer)count.get(obj);
            if (c == null) {
                count.put(obj, INTEGER_ONE);
                continue;
            }
            count.put(obj, new Integer(c + 1));
        }
        return count;
    }

    public static boolean isSubCollection(Collection a, Collection b) {
        Map mapa = CollectionUtils.getCardinalityMap(a);
        Map mapb = CollectionUtils.getCardinalityMap(b);
        Iterator it = a.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (CollectionUtils.getFreq(obj, mapa) <= CollectionUtils.getFreq(obj, mapb)) continue;
            return false;
        }
        return true;
    }

    public static boolean isProperSubCollection(Collection a, Collection b) {
        return a.size() < b.size() && CollectionUtils.isSubCollection(a, b);
    }

    public static boolean isEqualCollection(Collection a, Collection b) {
        if (a.size() != b.size()) {
            return false;
        }
        Map mapa = CollectionUtils.getCardinalityMap(a);
        Map mapb = CollectionUtils.getCardinalityMap(b);
        if (mapa.size() != mapb.size()) {
            return false;
        }
        Iterator it = mapa.keySet().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (CollectionUtils.getFreq(obj, mapa) == CollectionUtils.getFreq(obj, mapb)) continue;
            return false;
        }
        return true;
    }

    public static int cardinality(Object obj, Collection coll) {
        if (coll instanceof Set) {
            return coll.contains(obj) ? 1 : 0;
        }
        if (coll instanceof Bag) {
            return ((Bag)coll).getCount(obj);
        }
        int count = 0;
        if (obj == null) {
            Iterator it = coll.iterator();
            while (it.hasNext()) {
                if (it.next() != null) continue;
                ++count;
            }
        } else {
            Iterator it = coll.iterator();
            while (it.hasNext()) {
                if (!obj.equals(it.next())) continue;
                ++count;
            }
        }
        return count;
    }

    public static Object find(Collection collection, Predicate predicate) {
        if (collection != null && predicate != null) {
            Iterator iter = collection.iterator();
            while (iter.hasNext()) {
                Object item = iter.next();
                if (!predicate.evaluate(item)) continue;
                return item;
            }
        }
        return null;
    }

    public static void forAllDo(Collection collection, Closure closure) {
        if (collection != null && closure != null) {
            Iterator it = collection.iterator();
            while (it.hasNext()) {
                closure.execute(it.next());
            }
        }
    }

    public static void filter(Collection collection, Predicate predicate) {
        if (collection != null && predicate != null) {
            Iterator it = collection.iterator();
            while (it.hasNext()) {
                if (predicate.evaluate(it.next())) continue;
                it.remove();
            }
        }
    }

    public static void transform(Collection collection, Transformer transformer) {
        if (collection != null && transformer != null) {
            if (collection instanceof List) {
                List list = (List)collection;
                ListIterator<Object> it = list.listIterator();
                while (it.hasNext()) {
                    it.set(transformer.transform(it.next()));
                }
            } else {
                Collection resultCollection = CollectionUtils.collect(collection, transformer);
                collection.clear();
                collection.addAll(resultCollection);
            }
        }
    }

    public static int countMatches(Collection inputCollection, Predicate predicate) {
        int count = 0;
        if (inputCollection != null && predicate != null) {
            Iterator it = inputCollection.iterator();
            while (it.hasNext()) {
                if (!predicate.evaluate(it.next())) continue;
                ++count;
            }
        }
        return count;
    }

    public static boolean exists(Collection collection, Predicate predicate) {
        if (collection != null && predicate != null) {
            Iterator it = collection.iterator();
            while (it.hasNext()) {
                if (!predicate.evaluate(it.next())) continue;
                return true;
            }
        }
        return false;
    }

    public static Collection select(Collection inputCollection, Predicate predicate) {
        ArrayList answer = new ArrayList(inputCollection.size());
        CollectionUtils.select(inputCollection, predicate, answer);
        return answer;
    }

    public static void select(Collection inputCollection, Predicate predicate, Collection outputCollection) {
        if (inputCollection != null && predicate != null) {
            Iterator iter = inputCollection.iterator();
            while (iter.hasNext()) {
                Object item = iter.next();
                if (!predicate.evaluate(item)) continue;
                outputCollection.add(item);
            }
        }
    }

    public static Collection selectRejected(Collection inputCollection, Predicate predicate) {
        ArrayList answer = new ArrayList(inputCollection.size());
        CollectionUtils.selectRejected(inputCollection, predicate, answer);
        return answer;
    }

    public static void selectRejected(Collection inputCollection, Predicate predicate, Collection outputCollection) {
        if (inputCollection != null && predicate != null) {
            Iterator iter = inputCollection.iterator();
            while (iter.hasNext()) {
                Object item = iter.next();
                if (predicate.evaluate(item)) continue;
                outputCollection.add(item);
            }
        }
    }

    public static Collection collect(Collection inputCollection, Transformer transformer) {
        ArrayList answer = new ArrayList(inputCollection.size());
        CollectionUtils.collect(inputCollection, transformer, answer);
        return answer;
    }

    public static Collection collect(Iterator inputIterator, Transformer transformer) {
        ArrayList answer = new ArrayList();
        CollectionUtils.collect(inputIterator, transformer, answer);
        return answer;
    }

    public static Collection collect(Collection inputCollection, Transformer transformer, Collection outputCollection) {
        if (inputCollection != null) {
            return CollectionUtils.collect(inputCollection.iterator(), transformer, outputCollection);
        }
        return outputCollection;
    }

    public static Collection collect(Iterator inputIterator, Transformer transformer, Collection outputCollection) {
        if (inputIterator != null && transformer != null) {
            while (inputIterator.hasNext()) {
                Object item = inputIterator.next();
                Object value = transformer.transform(item);
                outputCollection.add(value);
            }
        }
        return outputCollection;
    }

    public static boolean addIgnoreNull(Collection collection, Object object) {
        return object == null ? false : collection.add(object);
    }

    public static void addAll(Collection collection, Iterator iterator) {
        while (iterator.hasNext()) {
            collection.add(iterator.next());
        }
    }

    public static void addAll(Collection collection, Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            collection.add(enumeration.nextElement());
        }
    }

    public static void addAll(Collection collection, Object[] elements) {
        int size = elements.length;
        for (int i = 0; i < size; ++i) {
            collection.add(elements[i]);
        }
    }

    public static Object index(Object obj, int idx) {
        return CollectionUtils.index(obj, new Integer(idx));
    }

    public static Object index(Object obj, Object index) {
        Map map;
        if (obj instanceof Map && (map = (Map)obj).containsKey(index)) {
            return map.get(index);
        }
        int idx = -1;
        if (index instanceof Integer) {
            idx = (Integer)index;
        }
        if (idx < 0) {
            return obj;
        }
        if (obj instanceof Map) {
            Map map2 = (Map)obj;
            Iterator iterator = map2.keySet().iterator();
            return CollectionUtils.index(iterator, idx);
        }
        if (obj instanceof List) {
            return ((List)obj).get(idx);
        }
        if (obj instanceof Object[]) {
            return ((Object[])obj)[idx];
        }
        if (obj instanceof Enumeration) {
            Enumeration it = (Enumeration)obj;
            while (it.hasMoreElements()) {
                if (--idx == -1) {
                    return it.nextElement();
                }
                it.nextElement();
            }
        } else {
            if (obj instanceof Iterator) {
                return CollectionUtils.index((Iterator)obj, idx);
            }
            if (obj instanceof Collection) {
                Iterator iterator = ((Collection)obj).iterator();
                return CollectionUtils.index(iterator, idx);
            }
        }
        return obj;
    }

    private static Object index(Iterator iterator, int idx) {
        while (iterator.hasNext()) {
            if (--idx == -1) {
                return iterator.next();
            }
            iterator.next();
        }
        return iterator;
    }

    public static Object get(Object object, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
        if (object instanceof Map) {
            Map map = (Map)object;
            Iterator iterator = map.entrySet().iterator();
            return CollectionUtils.get(iterator, index);
        }
        if (object instanceof List) {
            return ((List)object).get(index);
        }
        if (object instanceof Object[]) {
            return ((Object[])object)[index];
        }
        if (object instanceof Iterator) {
            Iterator it = (Iterator)object;
            while (it.hasNext()) {
                if (--index == -1) {
                    return it.next();
                }
                it.next();
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + index);
        }
        if (object instanceof Collection) {
            Iterator iterator = ((Collection)object).iterator();
            return CollectionUtils.get(iterator, index);
        }
        if (object instanceof Enumeration) {
            Enumeration it = (Enumeration)object;
            while (it.hasMoreElements()) {
                if (--index == -1) {
                    return it.nextElement();
                }
                it.nextElement();
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + index);
        }
        if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        }
        try {
            return Array.get(object, index);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    public static int size(Object object) {
        int total = 0;
        if (object instanceof Map) {
            total = ((Map)object).size();
        } else if (object instanceof Collection) {
            total = ((Collection)object).size();
        } else if (object instanceof Object[]) {
            total = ((Object[])object).length;
        } else if (object instanceof Iterator) {
            Iterator it = (Iterator)object;
            while (it.hasNext()) {
                ++total;
                it.next();
            }
        } else if (object instanceof Enumeration) {
            Enumeration it = (Enumeration)object;
            while (it.hasMoreElements()) {
                ++total;
                it.nextElement();
            }
        } else {
            if (object == null) {
                throw new IllegalArgumentException("Unsupported object type: null");
            }
            try {
                total = Array.getLength(object);
            }
            catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }
        return total;
    }

    public static boolean sizeIsEmpty(Object object) {
        if (object instanceof Collection) {
            return ((Collection)object).isEmpty();
        }
        if (object instanceof Map) {
            return ((Map)object).isEmpty();
        }
        if (object instanceof Object[]) {
            return ((Object[])object).length == 0;
        }
        if (object instanceof Iterator) {
            return !((Iterator)object).hasNext();
        }
        if (object instanceof Enumeration) {
            return !((Enumeration)object).hasMoreElements();
        }
        if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        }
        try {
            return Array.getLength(object) == 0;
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection coll) {
        return !CollectionUtils.isEmpty(coll);
    }

    public static void reverseArray(Object[] array) {
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    private static final int getFreq(Object obj, Map freqMap) {
        Integer count = (Integer)freqMap.get(obj);
        if (count != null) {
            return count;
        }
        return 0;
    }

    public static boolean isFull(Collection coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection)coll).isFull();
        }
        try {
            BoundedCollection bcoll = UnmodifiableBoundedCollection.decorateUsing(coll);
            return bcoll.isFull();
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static int maxSize(Collection coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection)coll).maxSize();
        }
        try {
            BoundedCollection bcoll = UnmodifiableBoundedCollection.decorateUsing(coll);
            return bcoll.maxSize();
        }
        catch (IllegalArgumentException ex) {
            return -1;
        }
    }

    public static Collection retainAll(Collection collection, Collection retain) {
        return ListUtils.retainAll(collection, retain);
    }

    public static Collection removeAll(Collection collection, Collection remove) {
        return ListUtils.removeAll(collection, remove);
    }

    public static Collection synchronizedCollection(Collection collection) {
        return SynchronizedCollection.decorate(collection);
    }

    public static Collection unmodifiableCollection(Collection collection) {
        return UnmodifiableCollection.decorate(collection);
    }

    public static Collection predicatedCollection(Collection collection, Predicate predicate) {
        return PredicatedCollection.decorate(collection, predicate);
    }

    public static Collection typedCollection(Collection collection, Class type) {
        return TypedCollection.decorate(collection, type);
    }

    public static Collection transformedCollection(Collection collection, Transformer transformer) {
        return TransformedCollection.decorate(collection, transformer);
    }
}

