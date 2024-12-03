/*
 * Decompiled with CFR 0.152.
 */
package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.cache.Node;
import com.github.benmanes.caffeine.cache.NodeFactory;
import com.github.benmanes.caffeine.cache.References;
import com.github.benmanes.caffeine.cache.UnsafeAccess;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

class FW<K, V>
extends Node<K, V>
implements NodeFactory<K, V> {
    protected static final long VALUE_OFFSET = UnsafeAccess.objectFieldOffset(FW.class, "value");
    volatile References.WeakValueReference<V> value;

    FW() {
    }

    FW(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
        this(new References.WeakKeyReference<K>(key, keyReferenceQueue), value, valueReferenceQueue, weight, now);
    }

    FW(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
        UnsafeAccess.UNSAFE.putObject(this, VALUE_OFFSET, new References.WeakValueReference<V>(keyReference, value, valueReferenceQueue));
    }

    @Override
    public final Object getKeyReference() {
        References.WeakValueReference valueRef = (References.WeakValueReference)this.getValueReference();
        return valueRef.getKeyReference();
    }

    @Override
    public final K getKey() {
        References.WeakValueReference valueRef = (References.WeakValueReference)this.getValueReference();
        References.WeakKeyReference keyRef = (References.WeakKeyReference)valueRef.getKeyReference();
        return (K)keyRef.get();
    }

    @Override
    public final V getValue() {
        Reference ref;
        Object referent;
        while ((referent = (ref = (Reference)UnsafeAccess.UNSAFE.getObject(this, VALUE_OFFSET)).get()) == null && ref != this.value) {
        }
        return (V)referent;
    }

    @Override
    public final Object getValueReference() {
        return UnsafeAccess.UNSAFE.getObject(this, VALUE_OFFSET);
    }

    @Override
    public final void setValue(V value, ReferenceQueue<V> referenceQueue) {
        Reference ref = (Reference)UnsafeAccess.UNSAFE.getObject(this, VALUE_OFFSET);
        UnsafeAccess.UNSAFE.putOrderedObject(this, VALUE_OFFSET, new References.WeakValueReference<V>(this.getKeyReference(), value, referenceQueue));
        ref.clear();
    }

    @Override
    public final boolean containsValue(Object value) {
        return this.getValue() == value;
    }

    @Override
    public Node<K, V> newNode(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
        return new FW<K, V>(key, keyReferenceQueue, value, valueReferenceQueue, weight, now);
    }

    @Override
    public Node<K, V> newNode(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
        return new FW<K, V>(keyReference, value, valueReferenceQueue, weight, now);
    }

    @Override
    public Object newLookupKey(Object key) {
        return new References.LookupKeyReference<Object>(key);
    }

    @Override
    public Object newReferenceKey(K key, ReferenceQueue<K> referenceQueue) {
        return new References.WeakKeyReference<K>(key, referenceQueue);
    }

    @Override
    public boolean weakValues() {
        return true;
    }

    @Override
    public final boolean isAlive() {
        Object key = this.getKeyReference();
        return key != RETIRED_WEAK_KEY && key != DEAD_WEAK_KEY;
    }

    @Override
    public final boolean isRetired() {
        return this.getKeyReference() == RETIRED_WEAK_KEY;
    }

    @Override
    public final void retire() {
        References.WeakValueReference valueRef = (References.WeakValueReference)this.getValueReference();
        References.WeakKeyReference keyRef = (References.WeakKeyReference)valueRef.getKeyReference();
        keyRef.clear();
        valueRef.setKeyReference(RETIRED_WEAK_KEY);
        valueRef.clear();
    }

    @Override
    public final boolean isDead() {
        return this.getKeyReference() == DEAD_WEAK_KEY;
    }

    @Override
    public final void die() {
        References.WeakValueReference valueRef = (References.WeakValueReference)this.getValueReference();
        References.WeakKeyReference keyRef = (References.WeakKeyReference)valueRef.getKeyReference();
        keyRef.clear();
        valueRef.setKeyReference(DEAD_WEAK_KEY);
        valueRef.clear();
    }
}

