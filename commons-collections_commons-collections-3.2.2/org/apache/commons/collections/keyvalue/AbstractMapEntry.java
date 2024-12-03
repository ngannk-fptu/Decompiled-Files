/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.keyvalue;

import java.util.Map;
import org.apache.commons.collections.keyvalue.AbstractKeyValue;

public abstract class AbstractMapEntry
extends AbstractKeyValue
implements Map.Entry {
    protected AbstractMapEntry(Object key, Object value) {
        super(key, value);
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
}

