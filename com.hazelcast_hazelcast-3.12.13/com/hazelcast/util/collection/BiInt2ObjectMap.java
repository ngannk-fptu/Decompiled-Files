/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.collection.Long2ObjectHashMap;
import com.hazelcast.util.function.Consumer;
import java.util.Map;

public class BiInt2ObjectMap<V> {
    private static final long LOWER_INT_MASK = 0xFFFFFFFFL;
    private final Long2ObjectHashMap<V> map;

    public BiInt2ObjectMap() {
        this.map = new Long2ObjectHashMap();
    }

    public BiInt2ObjectMap(int initialCapacity, double loadFactor) {
        this.map = new Long2ObjectHashMap(initialCapacity, loadFactor);
    }

    public int capacity() {
        return this.map.capacity();
    }

    public double loadFactor() {
        return this.map.loadFactor();
    }

    public V put(int keyPartA, int keyPartB, V value) {
        long key = BiInt2ObjectMap.compoundKey(keyPartA, keyPartB);
        return this.map.put(key, value);
    }

    public V get(int keyPartA, int keyPartB) {
        long key = BiInt2ObjectMap.compoundKey(keyPartA, keyPartB);
        return this.map.get(key);
    }

    public V remove(int keyPartA, int keyPartB) {
        long key = BiInt2ObjectMap.compoundKey(keyPartA, keyPartB);
        return this.map.remove(key);
    }

    public void forEach(EntryConsumer<V> consumer) {
        for (Map.Entry<Long, V> entry : this.map.entrySet()) {
            Long compoundKey = entry.getKey();
            int keyPartA = (int)(compoundKey >>> 32);
            int keyPartB = (int)(compoundKey & 0xFFFFFFFFL);
            consumer.accept(keyPartA, keyPartB, entry.getValue());
        }
    }

    public void forEach(Consumer<V> consumer) {
        for (Map.Entry<Long, V> entry : this.map.entrySet()) {
            consumer.accept(entry.getValue());
        }
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    private static long compoundKey(int keyPartA, int keyPartB) {
        return (long)keyPartA << 32 | (long)keyPartB;
    }

    public static interface EntryConsumer<V> {
        public void accept(int var1, int var2, V var3);
    }
}

