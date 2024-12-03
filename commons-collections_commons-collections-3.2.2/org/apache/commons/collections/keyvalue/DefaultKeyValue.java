/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.keyvalue;

import java.util.Map;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.AbstractKeyValue;
import org.apache.commons.collections.keyvalue.DefaultMapEntry;

public class DefaultKeyValue
extends AbstractKeyValue {
    public DefaultKeyValue() {
        super(null, null);
    }

    public DefaultKeyValue(Object key, Object value) {
        super(key, value);
    }

    public DefaultKeyValue(KeyValue pair) {
        super(pair.getKey(), pair.getValue());
    }

    public DefaultKeyValue(Map.Entry entry) {
        super(entry.getKey(), entry.getValue());
    }

    public Object setKey(Object key) {
        if (key == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a key.");
        }
        Object old = this.key;
        this.key = key;
        return old;
    }

    public Object setValue(Object value) {
        if (value == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a value.");
        }
        Object old = this.value;
        this.value = value;
        return old;
    }

    public Map.Entry toMapEntry() {
        return new DefaultMapEntry(this);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultKeyValue)) {
            return false;
        }
        DefaultKeyValue other = (DefaultKeyValue)obj;
        return (this.getKey() == null ? other.getKey() == null : this.getKey().equals(other.getKey())) && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()));
    }

    public int hashCode() {
        return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
    }
}

