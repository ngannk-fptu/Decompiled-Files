/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.hazelcast.serialization;

public class OsgiSafe<T> {
    private final T value;

    public OsgiSafe(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        T otherValue = ((OsgiSafe)o).value;
        return this.value == null ? otherValue == null : this.value.equals(otherValue);
    }

    public int hashCode() {
        return this.value == null ? 0 : this.value.hashCode();
    }
}

