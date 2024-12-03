/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Map;
import org.apache.commons.collections.KeyValue;

public class DefaultMapEntry
implements Map.Entry,
KeyValue {
    private Object key;
    private Object value;

    public DefaultMapEntry() {
    }

    public DefaultMapEntry(Map.Entry entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    public DefaultMapEntry(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return this.key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return this.value;
    }

    public Object setValue(Object value) {
        Object answer = this.value;
        this.value = value;
        return answer;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        Map.Entry other = (Map.Entry)obj;
        return (this.getKey() == null ? other.getKey() == null : this.getKey().equals(other.getKey())) && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()));
    }

    public int hashCode() {
        return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
    }

    public String toString() {
        return "" + this.getKey() + "=" + this.getValue();
    }
}

