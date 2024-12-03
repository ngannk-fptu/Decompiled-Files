/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.collection;

import com.mchange.v2.lang.ObjectUtils;
import java.util.Map;

public class MapEntry
implements Map.Entry {
    Object key;
    Object value;

    public MapEntry(Object object, Object object2) {
        this.key = object;
        this.value = object2;
    }

    public Object getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

    public Object setValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry)object;
            return ObjectUtils.eqOrBothNull(this.key, entry.getKey()) && ObjectUtils.eqOrBothNull(this.value, entry.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashOrZero(this.key) ^ ObjectUtils.hashOrZero(this.value);
    }
}

