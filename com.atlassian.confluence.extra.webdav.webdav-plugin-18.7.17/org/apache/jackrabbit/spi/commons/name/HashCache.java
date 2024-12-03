/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

public class HashCache<T> {
    private final T[] array;

    public HashCache() {
        this(10);
    }

    public HashCache(int exponent) {
        this.array = new Object[2 << exponent];
    }

    public T get(T object) {
        int position = object.hashCode() & this.array.length - 1;
        T previous = this.array[position];
        if (object.equals(previous)) {
            return previous;
        }
        this.array[position] = object;
        return object;
    }
}

