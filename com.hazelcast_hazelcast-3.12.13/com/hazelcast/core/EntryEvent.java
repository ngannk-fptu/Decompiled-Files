/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.core;

import com.hazelcast.core.AbstractIMapEvent;
import com.hazelcast.core.Member;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"SE_BAD_FIELD"})
public class EntryEvent<K, V>
extends AbstractIMapEvent {
    private static final long serialVersionUID = -2296203982913729851L;
    protected K key;
    protected V oldValue;
    protected V value;
    protected V mergingValue;

    public EntryEvent(Object source, Member member, int eventType, K key, V value) {
        this(source, member, eventType, key, null, value);
    }

    public EntryEvent(Object source, Member member, int eventType, K key, V oldValue, V value) {
        super(source, member, eventType);
        this.key = key;
        this.oldValue = oldValue;
        this.value = value;
    }

    public EntryEvent(Object source, Member member, int eventType, K key, V oldValue, V value, V mergingValue) {
        super(source, member, eventType);
        this.key = key;
        this.oldValue = oldValue;
        this.value = value;
        this.mergingValue = mergingValue;
    }

    public K getKey() {
        return this.key;
    }

    public V getOldValue() {
        return this.oldValue;
    }

    public V getValue() {
        return this.value;
    }

    public V getMergingValue() {
        return this.mergingValue;
    }

    @Override
    public String toString() {
        return "EntryEvent{" + super.toString() + ", key=" + this.getKey() + ", oldValue=" + this.getOldValue() + ", value=" + this.getValue() + ", mergingValue=" + this.getMergingValue() + '}';
    }
}

