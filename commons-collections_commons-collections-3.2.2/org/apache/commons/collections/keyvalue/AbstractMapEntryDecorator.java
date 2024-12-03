/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.keyvalue;

import java.util.Map;
import org.apache.commons.collections.KeyValue;

public abstract class AbstractMapEntryDecorator
implements Map.Entry,
KeyValue {
    protected final Map.Entry entry;

    public AbstractMapEntryDecorator(Map.Entry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Map Entry must not be null");
        }
        this.entry = entry;
    }

    protected Map.Entry getMapEntry() {
        return this.entry;
    }

    public Object getKey() {
        return this.entry.getKey();
    }

    public Object getValue() {
        return this.entry.getValue();
    }

    public Object setValue(Object object) {
        return this.entry.setValue(object);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return this.entry.equals(object);
    }

    public int hashCode() {
        return this.entry.hashCode();
    }

    public String toString() {
        return this.entry.toString();
    }
}

