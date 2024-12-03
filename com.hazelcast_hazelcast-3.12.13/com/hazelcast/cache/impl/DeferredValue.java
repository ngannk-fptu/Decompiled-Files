/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DeferredValue<V> {
    private static final DeferredValue NULL_VALUE;
    private volatile Data serializedValue;
    private volatile V value;
    private volatile boolean valueExists;
    private volatile boolean serializedValueExists;

    private DeferredValue() {
    }

    public V get(SerializationService serializationService) {
        if (!this.valueExists) {
            assert (serializationService != null);
            this.value = serializationService.toObject(this.serializedValue);
            this.valueExists = true;
        }
        return this.value;
    }

    public Data getSerializedValue(SerializationService serializationService) {
        if (!this.serializedValueExists) {
            assert (serializationService != null);
            this.serializedValue = serializationService.toData(this.value);
            this.serializedValueExists = true;
        }
        return this.serializedValue;
    }

    public DeferredValue<V> shallowCopy() {
        if (this == NULL_VALUE) {
            return NULL_VALUE;
        }
        DeferredValue<V> copy = new DeferredValue<V>();
        if (this.serializedValueExists) {
            copy.serializedValueExists = true;
            copy.serializedValue = this.serializedValue;
        }
        if (this.valueExists) {
            copy.valueExists = true;
            copy.value = this.value;
        }
        return copy;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DeferredValue deferredValue = (DeferredValue)o;
        if (this.valueExists && deferredValue.valueExists) {
            return this.value != null ? this.value.equals(deferredValue.value) : deferredValue.value == null;
        }
        if (this.serializedValueExists && deferredValue.serializedValueExists) {
            return this.serializedValue != null ? this.serializedValue.equals(deferredValue.serializedValue) : deferredValue.serializedValue == null;
        }
        throw new IllegalArgumentException("Cannot compare serialized vs deserialized value");
    }

    public int hashCode() {
        int result = this.serializedValue != null ? this.serializedValue.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (this.valueExists ? 1 : 0);
        result = 31 * result + (this.serializedValueExists ? 1 : 0);
        return result;
    }

    public static <V> DeferredValue<V> withSerializedValue(Data serializedValue) {
        if (serializedValue == null) {
            return NULL_VALUE;
        }
        DeferredValue<V> deferredValue = new DeferredValue<V>();
        deferredValue.serializedValue = serializedValue;
        deferredValue.serializedValueExists = true;
        return deferredValue;
    }

    public static <V> DeferredValue<V> withValue(V value) {
        if (value == null) {
            return NULL_VALUE;
        }
        DeferredValue<V> deferredValue = new DeferredValue<V>();
        deferredValue.value = value;
        deferredValue.valueExists = true;
        return deferredValue;
    }

    public static <V> DeferredValue<V> withNullValue() {
        return NULL_VALUE;
    }

    public static <V> Set<DeferredValue<V>> concurrentSetOfValues(Set<V> values) {
        Set<DeferredValue<V>> result = Collections.newSetFromMap(new ConcurrentHashMap());
        for (V value : values) {
            result.add(DeferredValue.withValue(value));
        }
        return result;
    }

    public static <V> Set<V> asPassThroughSet(Set<DeferredValue<V>> deferredValues, SerializationService serializationService) {
        return new DeferredValueSet<V>(serializationService, deferredValues);
    }

    static {
        DeferredValue nullValue = new DeferredValue();
        nullValue.valueExists = true;
        nullValue.serializedValueExists = true;
        NULL_VALUE = nullValue;
    }

    private static class DeferredValueIterator<V>
    implements Iterator<V> {
        private final SerializationService serializationService;
        private final Iterator<DeferredValue<V>> iterator;

        public DeferredValueIterator(SerializationService serializationService, Iterator<DeferredValue<V>> iterator) {
            this.serializationService = serializationService;
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public V next() {
            DeferredValue<V> next = this.iterator.next();
            return next.get(this.serializationService);
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }
    }

    private static class DeferredValueSet<V>
    extends AbstractSet<V> {
        private final SerializationService serializationService;
        private final Set<DeferredValue<V>> delegate;

        public DeferredValueSet(SerializationService serializationService, Set<DeferredValue<V>> delegate) {
            this.serializationService = serializationService;
            this.delegate = delegate;
        }

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public Iterator<V> iterator() {
            return new DeferredValueIterator<V>(this.serializationService, this.delegate.iterator());
        }

        @Override
        public boolean add(V v) {
            return this.delegate.add(DeferredValue.withValue(v));
        }

        @Override
        public boolean remove(Object o) {
            return this.delegate.remove(DeferredValue.withValue(o));
        }

        @Override
        public void clear() {
            this.delegate.clear();
        }

        private Collection<DeferredValue<?>> asDeferredValues(Collection<?> collection) {
            ArrayList deferredValues = new ArrayList();
            for (Object object : collection) {
                deferredValues.add(DeferredValue.withValue(object));
            }
            return deferredValues;
        }
    }
}

