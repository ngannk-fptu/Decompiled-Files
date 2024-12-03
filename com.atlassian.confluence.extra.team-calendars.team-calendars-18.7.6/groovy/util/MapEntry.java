/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import java.util.Map;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class MapEntry
implements Map.Entry {
    private Object key;
    private Object value;

    public MapEntry(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof MapEntry) {
            return this.equals((MapEntry)that);
        }
        return false;
    }

    public boolean equals(MapEntry that) {
        return DefaultTypeTransformation.compareEqual(this.key, that.key) && DefaultTypeTransformation.compareEqual(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return this.hash(this.key) ^ this.hash(this.value);
    }

    public String toString() {
        return "" + this.key + ":" + this.value;
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
        this.value = value;
        return value;
    }

    protected int hash(Object object) {
        return object == null ? 47806 : object.hashCode();
    }
}

