/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.DuplicateHandler;
import com.twelvemonkeys.util.FilterIterator;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class CollectionUtil {
    public static void main(String[] stringArray) {
        String string;
        int n = 1000;
        if (stringArray.length > 0) {
            n = Integer.parseInt(stringArray[0]);
        }
        String[] stringArray2 = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        stringArray2 = new String[]{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};
        System.out.println("\nFilterIterators:\n");
        List<String> list = Arrays.asList(stringArray2);
        FilterIterator<String> filterIterator = new FilterIterator<String>(list.iterator(), new FilterIterator.Filter(){

            public boolean accept(Object object) {
                return ((String)object).length() > 5;
            }
        });
        while (filterIterator.hasNext()) {
            string = (String)filterIterator.next();
            System.out.println(string + " has more than 5 letters!");
        }
        filterIterator = new FilterIterator<String>(list.iterator(), new FilterIterator.Filter(){

            public boolean accept(Object object) {
                return ((String)object).length() <= 5;
            }
        });
        while (filterIterator.hasNext()) {
            string = (String)filterIterator.next();
            System.out.println(string + " has less than, or exactly 5 letters!");
        }
        long l = System.currentTimeMillis();
        for (int i = 0; i < n; ++i) {
            filterIterator = new FilterIterator<String>(list.iterator(), new FilterIterator.Filter(){

                public boolean accept(Object object) {
                    return ((String)object).length() <= 5;
                }
            });
            while (filterIterator.hasNext()) {
                filterIterator.next();
                System.out.print("");
            }
        }
    }

    private CollectionUtil() {
    }

    public static Object mergeArrays(Object object, Object object2) {
        return CollectionUtil.mergeArrays(object, 0, Array.getLength(object), object2, 0, Array.getLength(object2));
    }

    public static Object mergeArrays(Object object, int n, int n2, Object object2, int n3, int n4) {
        Class<?> clazz = object.getClass();
        Class<?> clazz2 = clazz.getComponentType();
        Object object3 = Array.newInstance(clazz2, n2 + n4);
        System.arraycopy(object, n, object3, 0, n2);
        System.arraycopy(object2, n3, object3, n2, n4);
        return object3;
    }

    public static Object subArray(Object object, int n) {
        return CollectionUtil.subArray(object, n, -1);
    }

    public static <T> T[] subArray(T[] TArray, int n) {
        return CollectionUtil.subArray(TArray, n, -1);
    }

    public static Object subArray(Object object, int n, int n2) {
        Object object2;
        int n3;
        Validate.notNull(object, "array");
        if (n < 0) {
            throw new ArrayIndexOutOfBoundsException(n + " < 0");
        }
        Class<?> clazz = object.getClass().getComponentType();
        if (clazz == null) {
            throw new IllegalArgumentException("Not an array: " + object);
        }
        int n4 = Array.getLength(object);
        int n5 = n3 = n2 < 0 ? Math.max(0, n4 - n) : Math.min(n2, Math.max(0, n4 - n));
        if (n3 < n4) {
            object2 = Array.newInstance(clazz, n3);
            System.arraycopy(object, n, object2, 0, n3);
        } else {
            object2 = object;
        }
        return object2;
    }

    public static <T> T[] subArray(T[] TArray, int n, int n2) {
        return (Object[])CollectionUtil.subArray(TArray, n, n2);
    }

    public static <T> Iterator<T> iterator(final Enumeration<T> enumeration) {
        Validate.notNull(enumeration, "enumeration");
        return new Iterator<T>(){

            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <E> void addAll(Collection<E> collection, Iterator<? extends E> iterator) {
        while (iterator.hasNext()) {
            collection.add(iterator.next());
        }
    }

    public static <E> ListIterator<E> iterator(E[] EArray) {
        return CollectionUtil.iterator(EArray, 0, Validate.notNull(EArray).length);
    }

    public static <E> ListIterator<E> iterator(E[] EArray, int n, int n2) {
        return new ArrayIterator<E>(EArray, n, n2);
    }

    public static <K, V> Map<V, K> invert(Map<K, V> map) {
        return CollectionUtil.invert(map, null, null);
    }

    public static <K, V> Map<V, K> invert(Map<K, V> map, Map<V, K> map2, DuplicateHandler<K> duplicateHandler) {
        if (map == null) {
            throw new IllegalArgumentException("source == null");
        }
        Map map3 = map2;
        if (map3 == null) {
            try {
                map3 = (Map)map.getClass().newInstance();
            }
            catch (InstantiationException instantiationException) {
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
            if (map3 == null) {
                throw new IllegalArgumentException("result == null and source class " + map.getClass() + " cannot be instantiated.");
            }
        }
        Set<Map.Entry<K, V>> set = map.entrySet();
        for (Map.Entry<K, V> entry : set) {
            V v = entry.getValue();
            K k = entry.getKey();
            if (map3.containsKey(v)) {
                if (duplicateHandler != null) {
                    k = duplicateHandler.resolve(map3.get(v), k);
                } else {
                    throw new IllegalArgumentException("Result would include duplicate keys, but no DuplicateHandler specified.");
                }
            }
            map3.put(v, k);
        }
        return map3;
    }

    public static <T> Comparator<T> reverseOrder(Comparator<T> comparator) {
        return new ReverseComparator<T>(comparator);
    }

    static <T extends Iterator<? super E>, E> T generify(Iterator<?> iterator, Class<E> clazz) {
        return (T)iterator;
    }

    static <T extends Collection<? super E>, E> T generify(Collection<?> collection, Class<E> clazz) {
        return (T)collection;
    }

    static <T extends Map<? super K, ? super V>, K, V> T generify(Map<?, ?> map, Class<K> clazz, Class<V> clazz2) {
        return (T)map;
    }

    static <T extends Collection<? super E>, E> T generify2(Collection<?> collection) {
        return (T)collection;
    }

    private static class ArrayIterator<E>
    implements ListIterator<E> {
        private int next;
        private final int start;
        private final int length;
        private final E[] array;

        public ArrayIterator(E[] EArray, int n, int n2) {
            this.array = Validate.notNull(EArray, "array");
            this.start = Validate.isTrue(n >= 0, n, "start < 0: %d");
            this.length = Validate.isTrue(n2 <= EArray.length - n, n2, "length > array.length - start: %d");
            this.next = this.start;
        }

        @Override
        public boolean hasNext() {
            return this.next < this.length + this.start;
        }

        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                return this.array[this.next++];
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                NoSuchElementException noSuchElementException = new NoSuchElementException(arrayIndexOutOfBoundsException.getMessage());
                noSuchElementException.initCause(arrayIndexOutOfBoundsException);
                throw noSuchElementException;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPrevious() {
            return this.next > this.start;
        }

        @Override
        public int nextIndex() {
            return this.next - this.start;
        }

        @Override
        public E previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            try {
                return this.array[--this.next];
            }
            catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                NoSuchElementException noSuchElementException = new NoSuchElementException(arrayIndexOutOfBoundsException.getMessage());
                noSuchElementException.initCause(arrayIndexOutOfBoundsException);
                throw noSuchElementException;
            }
        }

        @Override
        public int previousIndex() {
            return this.nextIndex() - 1;
        }

        @Override
        public void set(E e) {
            this.array[this.next - 1] = e;
        }
    }

    private static class ReverseComparator<T>
    implements Comparator<T> {
        private final Comparator<T> comparator;

        public ReverseComparator(Comparator<T> comparator) {
            this.comparator = Validate.notNull(comparator);
        }

        @Override
        public int compare(T t, T t2) {
            int n = this.comparator.compare(t, t2);
            return -(n | n >>> 1);
        }
    }
}

