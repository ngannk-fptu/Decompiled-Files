/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject.util;

import com.opensymphony.xwork2.inject.util.FinalizableSoftReference;
import com.opensymphony.xwork2.inject.util.FinalizableWeakReference;
import com.opensymphony.xwork2.inject.util.ReferenceType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReferenceMap<K, V>
implements Map<K, V>,
Serializable {
    private static final long serialVersionUID = 0L;
    transient ConcurrentMap<Object, Object> delegate;
    final ReferenceType keyReferenceType;
    final ReferenceType valueReferenceType;
    private static PutStrategy defaultPutStrategy;

    public ReferenceMap(ReferenceType keyReferenceType, ReferenceType valueReferenceType) {
        ReferenceMap.ensureNotNull(new Object[]{keyReferenceType, valueReferenceType});
        if (keyReferenceType == ReferenceType.PHANTOM || valueReferenceType == ReferenceType.PHANTOM) {
            throw new IllegalArgumentException("Phantom references not supported.");
        }
        this.delegate = new ConcurrentHashMap<Object, Object>();
        this.keyReferenceType = keyReferenceType;
        this.valueReferenceType = valueReferenceType;
    }

    V internalGet(K key) {
        Object valueReference = this.delegate.get(this.makeKeyReferenceAware(key));
        return valueReference == null ? null : (V)this.dereferenceValue(valueReference);
    }

    @Override
    public V get(Object key) {
        ReferenceMap.ensureNotNull(key);
        return this.internalGet(key);
    }

    V execute(Strategy strategy, K key, V value) {
        ReferenceMap.ensureNotNull(key, value);
        Object keyReference = this.referenceKey(key);
        Object valueReference = strategy.execute(this, keyReference, this.referenceValue(keyReference, value));
        return valueReference == null ? null : (V)this.dereferenceValue(valueReference);
    }

    @Override
    public V put(K key, V value) {
        return this.execute(this.putStrategy(), key, value);
    }

    @Override
    public V remove(Object key) {
        ReferenceMap.ensureNotNull(key);
        Object referenceAwareKey = this.makeKeyReferenceAware(key);
        Object valueReference = this.delegate.remove(referenceAwareKey);
        return valueReference == null ? null : (V)this.dereferenceValue(valueReference);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        ReferenceMap.ensureNotNull(key);
        Object referenceAwareKey = this.makeKeyReferenceAware(key);
        return this.delegate.containsKey(referenceAwareKey);
    }

    @Override
    public boolean containsValue(Object value) {
        ReferenceMap.ensureNotNull(value);
        for (Object valueReference : this.delegate.values()) {
            if (!value.equals(this.dereferenceValue(valueReference))) continue;
            return true;
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<K, V> entry : t.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.dereferenceKeySet(this.delegate.keySet()));
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(this.dereferenceValues(this.delegate.values()));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return this.execute(this.putIfAbsentStrategy(), key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        ReferenceMap.ensureNotNull(key, value);
        Object referenceAwareKey = this.makeKeyReferenceAware(key);
        Object referenceAwareValue = this.makeValueReferenceAware(value);
        return this.delegate.remove(referenceAwareKey, referenceAwareValue);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        ReferenceMap.ensureNotNull(key, oldValue, newValue);
        Object keyReference = this.referenceKey(key);
        Object referenceAwareOldValue = this.makeValueReferenceAware(oldValue);
        return this.delegate.replace(keyReference, referenceAwareOldValue, this.referenceValue(keyReference, newValue));
    }

    @Override
    public V replace(K key, V value) {
        return this.execute(this.replaceStrategy(), key, value);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        HashSet<Entry> entrySet = new HashSet<Entry>();
        for (Map.Entry<Object, Object> entry : this.delegate.entrySet()) {
            Entry dereferenced = this.dereferenceEntry(entry);
            if (dereferenced == null) continue;
            entrySet.add(dereferenced);
        }
        return Collections.unmodifiableSet(entrySet);
    }

    Entry dereferenceEntry(Map.Entry<Object, Object> entry) {
        K key = this.dereferenceKey(entry.getKey());
        V value = this.dereferenceValue(entry.getValue());
        return key == null || value == null ? null : new Entry(key, value);
    }

    Object referenceKey(K key) {
        switch (this.keyReferenceType) {
            case STRONG: {
                return key;
            }
            case SOFT: {
                return new SoftKeyReference(key);
            }
            case WEAK: {
                return new WeakKeyReference(key);
            }
        }
        throw new AssertionError();
    }

    K dereferenceKey(Object o) {
        return (K)this.dereference(this.keyReferenceType, o);
    }

    V dereferenceValue(Object o) {
        return (V)this.dereference(this.valueReferenceType, o);
    }

    Object dereference(ReferenceType referenceType, Object reference) {
        return referenceType == ReferenceType.STRONG ? reference : ((Reference)reference).get();
    }

    Object referenceValue(Object keyReference, Object value) {
        switch (this.valueReferenceType) {
            case STRONG: {
                return value;
            }
            case SOFT: {
                return new SoftValueReference(keyReference, value);
            }
            case WEAK: {
                return new WeakValueReference(keyReference, value);
            }
        }
        throw new AssertionError();
    }

    Set<K> dereferenceKeySet(Set keyReferences) {
        return this.keyReferenceType == ReferenceType.STRONG ? keyReferences : (Set)this.dereferenceCollection(this.keyReferenceType, keyReferences, new HashSet());
    }

    Collection<V> dereferenceValues(Collection valueReferences) {
        return this.valueReferenceType == ReferenceType.STRONG ? valueReferences : this.dereferenceCollection(this.valueReferenceType, valueReferences, new ArrayList(valueReferences.size()));
    }

    Object makeKeyReferenceAware(Object o) {
        return this.keyReferenceType == ReferenceType.STRONG ? o : new KeyReferenceAwareWrapper(o);
    }

    Object makeValueReferenceAware(Object o) {
        return this.valueReferenceType == ReferenceType.STRONG ? o : new ReferenceAwareWrapper(o);
    }

    <T extends Collection<Object>> T dereferenceCollection(ReferenceType referenceType, T in, T out) {
        for (Object reference : in) {
            out.add((Object)this.dereference(referenceType, reference));
        }
        return out;
    }

    static int keyHashCode(Object key) {
        return System.identityHashCode(key);
    }

    static boolean referenceEquals(Reference r, Object o) {
        if (o instanceof InternalReference) {
            if (o == r) {
                return true;
            }
            Object referent = ((Reference)o).get();
            return referent != null && referent == r.get();
        }
        return ((ReferenceAwareWrapper)o).unwrap() == r.get();
    }

    protected Strategy putStrategy() {
        return PutStrategy.PUT;
    }

    protected Strategy putIfAbsentStrategy() {
        return PutStrategy.PUT_IF_ABSENT;
    }

    protected Strategy replaceStrategy() {
        return PutStrategy.REPLACE;
    }

    protected PutStrategy getPutStrategy() {
        return defaultPutStrategy;
    }

    static void ensureNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    static void ensureNotNull(Object ... array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) continue;
            throw new NullPointerException("Argument #" + i + " is null.");
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.size());
        for (Map.Entry entry : this.delegate.entrySet()) {
            K key = this.dereferenceKey(entry.getKey());
            V value = this.dereferenceValue(entry.getValue());
            if (key == null || value == null) continue;
            out.writeObject(key);
            out.writeObject(value);
        }
        out.writeObject(null);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object key;
        in.defaultReadObject();
        int size = in.readInt();
        this.delegate = new ConcurrentHashMap<Object, Object>(size);
        while ((key = in.readObject()) != null) {
            Object value = in.readObject();
            this.put(key, value);
        }
    }

    class Entry
    implements Map.Entry<K, V> {
        K key;
        V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            return ReferenceMap.this.put(this.key, value);
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() * 31 + this.value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry)o;
            return this.key.equals(entry.key) && this.value.equals(entry.value);
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static enum PutStrategy implements Strategy
    {
        PUT{

            @Override
            public Object execute(ReferenceMap map, Object keyReference, Object valueReference) {
                return map.delegate.put(keyReference, valueReference);
            }
        }
        ,
        REPLACE{

            @Override
            public Object execute(ReferenceMap map, Object keyReference, Object valueReference) {
                return map.delegate.replace(keyReference, valueReference);
            }
        }
        ,
        PUT_IF_ABSENT{

            @Override
            public Object execute(ReferenceMap map, Object keyReference, Object valueReference) {
                return map.delegate.putIfAbsent(keyReference, valueReference);
            }
        };

    }

    protected static interface Strategy {
        public Object execute(ReferenceMap var1, Object var2, Object var3);
    }

    class WeakValueReference
    extends FinalizableWeakReference<Object>
    implements InternalReference {
        Object keyReference;

        public WeakValueReference(Object keyReference, Object value) {
            super(value);
            this.keyReference = keyReference;
        }

        @Override
        public void finalizeReferent() {
            ReferenceMap.this.delegate.remove(this.keyReference, this);
        }

        public boolean equals(Object obj) {
            return ReferenceMap.referenceEquals(this, obj);
        }
    }

    class SoftValueReference
    extends FinalizableSoftReference<Object>
    implements InternalReference {
        Object keyReference;

        public SoftValueReference(Object keyReference, Object value) {
            super(value);
            this.keyReference = keyReference;
        }

        @Override
        public void finalizeReferent() {
            ReferenceMap.this.delegate.remove(this.keyReference, this);
        }

        public boolean equals(Object obj) {
            return ReferenceMap.referenceEquals(this, obj);
        }
    }

    class WeakKeyReference
    extends FinalizableWeakReference<Object>
    implements InternalReference {
        int hashCode;

        public WeakKeyReference(Object key) {
            super(key);
            this.hashCode = ReferenceMap.keyHashCode(key);
        }

        @Override
        public void finalizeReferent() {
            ReferenceMap.this.delegate.remove(this);
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object o) {
            return ReferenceMap.referenceEquals(this, o);
        }
    }

    class SoftKeyReference
    extends FinalizableSoftReference<Object>
    implements InternalReference {
        int hashCode;

        public SoftKeyReference(Object key) {
            super(key);
            this.hashCode = ReferenceMap.keyHashCode(key);
        }

        @Override
        public void finalizeReferent() {
            ReferenceMap.this.delegate.remove(this);
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object o) {
            return ReferenceMap.referenceEquals(this, o);
        }
    }

    static class KeyReferenceAwareWrapper
    extends ReferenceAwareWrapper {
        public KeyReferenceAwareWrapper(Object wrapped) {
            super(wrapped);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.wrapped);
        }
    }

    static class ReferenceAwareWrapper {
        Object wrapped;

        ReferenceAwareWrapper(Object wrapped) {
            this.wrapped = wrapped;
        }

        Object unwrap() {
            return this.wrapped;
        }

        public int hashCode() {
            return this.wrapped.hashCode();
        }

        public boolean equals(Object obj) {
            return obj.equals(this);
        }
    }

    static interface InternalReference {
    }
}

