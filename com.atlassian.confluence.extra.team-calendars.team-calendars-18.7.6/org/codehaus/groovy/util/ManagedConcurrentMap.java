/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import org.codehaus.groovy.util.AbstractConcurrentMap;
import org.codehaus.groovy.util.ManagedReference;
import org.codehaus.groovy.util.ReferenceBundle;

public class ManagedConcurrentMap<K, V>
extends AbstractConcurrentMap<K, V> {
    protected ReferenceBundle bundle;

    public ManagedConcurrentMap(ReferenceBundle bundle) {
        super(bundle);
        this.bundle = bundle;
        if (bundle == null) {
            throw new IllegalArgumentException("bundle must not be null");
        }
    }

    @Override
    protected Segment<K, V> createSegment(Object segmentInfo, int cap) {
        ReferenceBundle bundle = (ReferenceBundle)segmentInfo;
        if (bundle == null) {
            throw new IllegalArgumentException("bundle must not be null");
        }
        return new Segment(bundle, cap);
    }

    public static class EntryWithValue<K, V>
    extends Entry<K, V> {
        private V value;

        public EntryWithValue(ReferenceBundle bundle, Segment segment, K key, int hash, V value) {
            super(bundle, segment, key, hash);
            this.setValue(value);
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public void finalizeReference() {
            this.value = null;
            super.finalizeReference();
        }
    }

    public static class Entry<K, V>
    extends ManagedReference<K>
    implements AbstractConcurrentMap.Entry<K, V> {
        private final Segment segment;
        private int hash;

        public Entry(ReferenceBundle bundle, Segment segment, K key, int hash) {
            super(bundle, key);
            this.segment = segment;
            this.hash = hash;
        }

        @Override
        public boolean isValid() {
            return this.get() != null;
        }

        @Override
        public boolean isEqual(K key, int hash) {
            return this.hash == hash && this.get() == key;
        }

        @Override
        public V getValue() {
            return (V)this;
        }

        @Override
        public void setValue(V value) {
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public void finalizeReference() {
            this.segment.removeEntry(this);
            super.finalizeReference();
        }

        @Deprecated
        public void finalizeRef() {
            this.finalizeReference();
        }
    }

    public static class Segment<K, V>
    extends AbstractConcurrentMap.Segment<K, V> {
        protected final ReferenceBundle bundle;

        public Segment(ReferenceBundle bundle, int cap) {
            super(cap);
            this.bundle = bundle;
            if (bundle == null) {
                throw new IllegalArgumentException("bundle must not be null");
            }
        }

        @Override
        protected AbstractConcurrentMap.Entry<K, V> createEntry(K key, int hash, V value) {
            if (this.bundle == null) {
                throw new IllegalArgumentException("bundle must not be null");
            }
            return new EntryWithValue<K, V>(this.bundle, this, key, hash, value);
        }
    }
}

