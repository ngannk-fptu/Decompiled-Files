/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

public class MutableLong {
    public long value;

    public static MutableLong valueOf(long value) {
        MutableLong instance = new MutableLong();
        instance.value = value;
        return instance;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MutableLong that = (MutableLong)o;
        return this.value == that.value;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    public String toString() {
        return "MutableLong{value=" + this.value + '}';
    }
}

