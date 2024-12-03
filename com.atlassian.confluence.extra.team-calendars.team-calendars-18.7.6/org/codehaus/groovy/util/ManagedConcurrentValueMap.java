/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.groovy.util.ManagedReference;
import org.codehaus.groovy.util.ReferenceBundle;

public class ManagedConcurrentValueMap<K, V> {
    private final ConcurrentHashMap<K, ManagedReference<V>> internalMap;
    private ReferenceBundle bundle;

    public ManagedConcurrentValueMap(ReferenceBundle bundle) {
        this.bundle = bundle;
        this.internalMap = new ConcurrentHashMap();
    }

    public void setBundle(ReferenceBundle bundle) {
        this.bundle = bundle;
    }

    public V get(K key) {
        ManagedReference<V> ref = this.internalMap.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public void put(final K key, V value) {
        ManagedReference ref = new ManagedReference<V>(this.bundle, value){

            @Override
            public void finalizeReference() {
                ManagedConcurrentValueMap.this.internalMap.remove(key, this);
                super.finalizeReference();
            }
        };
        this.internalMap.put(key, ref);
    }
}

