/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.keyvalue;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections.KeyValue;

public class TiedMapEntry
implements Map.Entry,
KeyValue,
Serializable {
    private static final long serialVersionUID = -8453869361373831205L;
    private final Map map;
    private final Object key;

    public TiedMapEntry(Map map, Object key) {
        this.map = map;
        this.key = key;
    }

    public Object getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.map.get(this.key);
    }

    public Object setValue(Object value) {
        if (value == this) {
            throw new IllegalArgumentException("Cannot set value to this map entry");
        }
        return this.map.put(this.key, value);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        Map.Entry other = (Map.Entry)obj;
        Object value = this.getValue();
        return (this.key == null ? other.getKey() == null : this.key.equals(other.getKey())) && (value == null ? other.getValue() == null : value.equals(other.getValue()));
    }

    public int hashCode() {
        Object value = this.getValue();
        return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}

