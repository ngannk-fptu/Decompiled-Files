/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

public class IDKey {
    private Object value = null;
    private int id = 0;

    public IDKey(Object _value) {
        this.id = System.identityHashCode(_value);
        this.value = _value;
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object other) {
        if (!(other instanceof IDKey)) {
            return false;
        }
        IDKey idKey = (IDKey)other;
        if (this.id != idKey.id) {
            return false;
        }
        return this.value == idKey.value;
    }
}

