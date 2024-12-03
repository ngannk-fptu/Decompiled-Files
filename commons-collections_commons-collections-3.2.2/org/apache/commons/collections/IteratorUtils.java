/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedIterator;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.ResettableListIterator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.EmptyListIterator;
import org.apache.commons.collections.iterators.EmptyMapIterator;
import org.apache.commons.collections.iterators.EmptyOrderedIterator;
import org.apache.commons.collections.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections.iterators.EnumerationIterator;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.iterators.FilterListIterator;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.commons.collections.iterators.LoopingIterator;
import org.apache.commons.collections.iterators.LoopingListIterator;
import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.apache.commons.collections.iterators.ObjectArrayListIterator;
import org.apache.commons.collections.iterators.ObjectGraphIterator;
import org.apache.commons.collections.iterators.SingletonIterator;
import org.apache.commons.collections.iterators.SingletonListIterator;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import org.apache.commons.collections.iterators.UnmodifiableListIterator;
import org.apache.commons.collections.iterators.UnmodifiableMapIterator;

public class IteratorUtils {
    public static final ResettableIterator EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;
    public static final ResettableListIterator EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;
    public static final OrderedIterator EMPTY_ORDERED_ITERATOR = EmptyOrderedIterator.INSTANCE;
    public static final MapIterator EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;
    public static final OrderedMapIterator EMPTY_ORDERED_MAP_ITERATOR = EmptyOrderedMapIterator.INSTANCE;
    static /* synthetic */ Class class$java$util$Iterator;

    public static ResettableIterator emptyIterator() {
        return EMPTY_ITERATOR;
    }

    public static ResettableListIterator emptyListIterator() {
        return EMPTY_LIST_ITERATOR;
    }

    public static OrderedIterator emptyOrderedIterator() {
        return EMPTY_ORDERED_ITERATOR;
    }

    public static MapIterator emptyMapIterator() {
        return EMPTY_MAP_ITERATOR;
    }

    public static OrderedMapIterator emptyOrderedMapIterator() {
        return EMPTY_ORDERED_MAP_ITERATOR;
    }

    public static ResettableIterator singletonIterator(Object object) {
        return new SingletonIterator(object);
    }

    public static ListIterator singletonListIterator(Object object) {
        return new SingletonListIterator(object);
    }

    public static ResettableIterator arrayIterator(Object[] array) {
        return new ObjectArrayIterator(array);
    }

    public static ResettableIterator arrayIterator(Object array) {
        return new ArrayIterator(array);
    }

    public static ResettableIterator arrayIterator(Object[] array, int start) {
        return new ObjectArrayIterator(array, start);
    }

    public static ResettableIterator arrayIterator(Object array, int start) {
        return new ArrayIterator(array, start);
    }

    public static ResettableIterator arrayIterator(Object[] array, int start, int end) {
        return new ObjectArrayIterator(array, start, end);
    }

    public static ResettableIterator arrayIterator(Object array, int start, int end) {
        return new ArrayIterator(array, start, end);
    }

    public static ResettableListIterator arrayListIterator(Object[] array) {
        return new ObjectArrayListIterator(array);
    }

    public static ResettableListIterator arrayListIterator(Object array) {
        return new ArrayListIterator(array);
    }

    public static ResettableListIterator arrayListIterator(Object[] array, int start) {
        return new ObjectArrayListIterator(array, start);
    }

    public static ResettableListIterator arrayListIterator(Object array, int start) {
        return new ArrayListIterator(array, start);
    }

    public static ResettableListIterator arrayListIterator(Object[] array, int start, int end) {
        return new ObjectArrayListIterator(array, start, end);
    }

    public static ResettableListIterator arrayListIterator(Object array, int start, int end) {
        return new ArrayListIterator(array, start, end);
    }

    public static Iterator unmodifiableIterator(Iterator iterator) {
        return UnmodifiableIterator.decorate(iterator);
    }

    public static ListIterator unmodifiableListIterator(ListIterator listIterator) {
        return UnmodifiableListIterator.decorate(listIterator);
    }

    public static MapIterator unmodifiableMapIterator(MapIterator mapIterator) {
        return UnmodifiableMapIterator.decorate(mapIterator);
    }

    public static Iterator chainedIterator(Iterator iterator1, Iterator iterator2) {
        return new IteratorChain(iterator1, iterator2);
    }

    public static Iterator chainedIterator(Iterator[] iterators) {
        return new IteratorChain(iterators);
    }

    public static Iterator chainedIterator(Collection iterators) {
        return new IteratorChain(iterators);
    }

    public static Iterator collatedIterator(Comparator comparator, Iterator iterator1, Iterator iterator2) {
        return new CollatingIterator(comparator, iterator1, iterator2);
    }

    public static Iterator collatedIterator(Comparator comparator, Iterator[] iterators) {
        return new CollatingIterator(comparator, iterators);
    }

    public static Iterator collatedIterator(Comparator comparator, Collection iterators) {
        return new CollatingIterator(comparator, iterators);
    }

    public static Iterator objectGraphIterator(Object root, Transformer transformer) {
        return new ObjectGraphIterator(root, transformer);
    }

    public static Iterator transformedIterator(Iterator iterator, Transformer transform) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (transform == null) {
            throw new NullPointerException("Transformer must not be null");
        }
        return new TransformIterator(iterator, transform);
    }

    public static Iterator filteredIterator(Iterator iterator, Predicate predicate) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterIterator(iterator, predicate);
    }

    public static ListIterator filteredListIterator(ListIterator listIterator, Predicate predicate) {
        if (listIterator == null) {
            throw new NullPointerException("ListIterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterListIterator(listIterator, predicate);
    }

    public static ResettableIterator loopingIterator(Collection coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new LoopingIterator(coll);
    }

    public static ResettableListIterator loopingListIterator(List list) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        return new LoopingListIterator(list);
    }

    public static Iterator asIterator(Enumeration enumeration) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        return new EnumerationIterator(enumeration);
    }

    public static Iterator asIterator(Enumeration enumeration, Collection removeCollection) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        if (removeCollection == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new EnumerationIterator(enumeration, removeCollection);
    }

    public static Enumeration asEnumeration(Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorEnumeration(iterator);
    }

    public static ListIterator toListIterator(Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new ListIteratorWrapper(iterator);
    }

    public static Object[] toArray(Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        List list = IteratorUtils.toList(iterator, 100);
        return list.toArray();
    }

    public static Object[] toArray(Iterator iterator, Class arrayClass) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (arrayClass == null) {
            throw new NullPointerException("Array class must not be null");
        }
        List list = IteratorUtils.toList(iterator, 100);
        return list.toArray((Object[])Array.newInstance(arrayClass, list.size()));
    }

    public static List toList(Iterator iterator) {
        return IteratorUtils.toList(iterator, 10);
    }

    public static List toList(Iterator iterator, int estimatedSize) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        ArrayList list = new ArrayList(estimatedSize);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public static Iterator getIterator(Object obj) {
        if (obj == null) {
            return IteratorUtils.emptyIterator();
        }
        if (obj instanceof Iterator) {
            return (Iterator)obj;
        }
        if (obj instanceof Collection) {
            return ((Collection)obj).iterator();
        }
        if (obj instanceof Object[]) {
            return new ObjectArrayIterator((Object[])obj);
        }
        if (obj instanceof Enumeration) {
            return new EnumerationIterator((Enumeration)obj);
        }
        if (obj instanceof Map) {
            return ((Map)obj).values().iterator();
        }
        if (obj instanceof Dictionary) {
            return new EnumerationIterator(((Dictionary)obj).elements());
        }
        if (obj != null && obj.getClass().isArray()) {
            return new ArrayIterator(obj);
        }
        try {
            Iterator it;
            Method method = obj.getClass().getMethod("iterator", null);
            if ((class$java$util$Iterator == null ? (class$java$util$Iterator = IteratorUtils.class$("java.util.Iterator")) : class$java$util$Iterator).isAssignableFrom(method.getReturnType()) && (it = (Iterator)method.invoke(obj, (Object[])null)) != null) {
                return it;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return IteratorUtils.singletonIterator(obj);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

