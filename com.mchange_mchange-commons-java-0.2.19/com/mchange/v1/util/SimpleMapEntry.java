/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.AbstractMapEntry;
import java.util.Map;

public class SimpleMapEntry
extends AbstractMapEntry
implements Map.Entry {
    Object key;
    Object value;

    public SimpleMapEntry(Object object, Object object2) {
        this.key = object;
        this.value = object2;
    }

    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public Object setValue(Object object) {
        Object object2 = object;
        this.value = object;
        return object2;
    }
}

