/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.atlassian.cache.hazelcast;

import com.google.common.base.Objects;

class Versioned<T> {
    private static final Versioned EMPTY = new Versioned<Object>(null, 0L);
    private final long version;
    private final T value;

    Versioned(T value, long version) {
        this.value = value;
        this.version = version;
    }

    public static <T> Versioned<T> empty() {
        return EMPTY;
    }

    public T getValue() {
        return this.value;
    }

    public long getVersion() {
        return this.version;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Versioned other = (Versioned)o;
        return this.version == other.version && Objects.equal(this.value, other.value);
    }

    public int hashCode() {
        int result = (int)(this.version ^ this.version >>> 32);
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Versioned[version=" + this.version + "; value=" + this.value + ']';
    }
}

