/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Hashtable;

@Deprecated
public class LazyHashtable<K, V>
extends Hashtable<K, V> {
    protected boolean initAllDone = false;

    protected void initAll() {
        if (this.initAllDone) {
            return;
        }
        this.initAllDone = true;
    }

    @Override
    public Enumeration<V> elements() {
        this.initAll();
        return super.elements();
    }

    @Override
    public boolean isEmpty() {
        this.initAll();
        return super.isEmpty();
    }

    @Override
    public int size() {
        this.initAll();
        return super.size();
    }

    @Override
    public boolean contains(Object value) {
        this.initAll();
        return super.contains(value);
    }

    @Override
    public boolean containsKey(Object value) {
        this.initAll();
        return super.containsKey(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.contains(value);
    }

    @Override
    public Enumeration<K> keys() {
        this.initAll();
        return super.keys();
    }
}

