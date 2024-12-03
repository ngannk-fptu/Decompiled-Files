/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.mutable;

import java.io.Serializable;
import org.apache.commons.lang.mutable.Mutable;

public class MutableObject
implements Mutable,
Serializable {
    private static final long serialVersionUID = 86241875189L;
    private Object value;

    public MutableObject() {
    }

    public MutableObject(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MutableObject) {
            Object other = ((MutableObject)obj).value;
            return this.value == other || this.value != null && this.value.equals(other);
        }
        return false;
    }

    public int hashCode() {
        return this.value == null ? 0 : this.value.hashCode();
    }

    public String toString() {
        return this.value == null ? "null" : this.value.toString();
    }
}

