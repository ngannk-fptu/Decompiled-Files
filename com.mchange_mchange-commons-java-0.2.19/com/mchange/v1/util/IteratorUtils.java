/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class IteratorUtils {
    public static final Iterator EMPTY_ITERATOR = new Iterator(){

        @Override
        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    };

    public static Iterator oneElementUnmodifiableIterator(final Object object) {
        return new Iterator(){
            boolean shot = false;

            @Override
            public boolean hasNext() {
                return !this.shot;
            }

            public Object next() {
                if (this.shot) {
                    throw new NoSuchElementException();
                }
                this.shot = true;
                return object;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported.");
            }
        };
    }

    public static boolean equivalent(Iterator iterator, Iterator iterator2) {
        block3: {
            while (true) {
                Object e;
                boolean bl;
                boolean bl2;
                if ((bl2 = iterator.hasNext()) ^ (bl = iterator2.hasNext())) {
                    return false;
                }
                if (!bl2) break block3;
                Object e2 = iterator.next();
                if (e2 == (e = iterator2.next())) continue;
                if (e2 == null) {
                    return false;
                }
                if (!e2.equals(e)) break;
            }
            return false;
        }
        return true;
    }

    public static ArrayList toArrayList(Iterator iterator, int n) {
        ArrayList arrayList = new ArrayList(n);
        while (iterator.hasNext()) {
            arrayList.add(iterator.next());
        }
        return arrayList;
    }

    public static void fillArray(Iterator iterator, Object[] objectArray, boolean bl) {
        int n = 0;
        int n2 = objectArray.length;
        while (n < n2 && iterator.hasNext()) {
            objectArray[n++] = iterator.next();
        }
        if (bl && n < n2) {
            objectArray[n] = null;
        }
    }

    public static void fillArray(Iterator iterator, Object[] objectArray) {
        IteratorUtils.fillArray(iterator, objectArray, false);
    }

    public static Object[] toArray(Iterator iterator, int n, Class clazz, boolean bl) {
        Object[] objectArray = (Object[])Array.newInstance(clazz, n);
        IteratorUtils.fillArray(iterator, objectArray, bl);
        return objectArray;
    }

    public static Object[] toArray(Iterator iterator, int n, Class clazz) {
        return IteratorUtils.toArray(iterator, n, clazz, false);
    }

    public static Object[] toArray(Iterator iterator, int n, Object[] objectArray) {
        if (objectArray.length >= n) {
            IteratorUtils.fillArray(iterator, objectArray, true);
            return objectArray;
        }
        Class<?> clazz = objectArray.getClass().getComponentType();
        return IteratorUtils.toArray(iterator, n, clazz);
    }

    private IteratorUtils() {
    }
}

