/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

public class CBORSimpleValue {
    protected final int _value;

    public CBORSimpleValue(int value) {
        this._value = value;
    }

    public int getValue() {
        return this._value;
    }

    public int hashCode() {
        return this._value;
    }

    public String toString() {
        return Integer.valueOf(this._value).toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CBORSimpleValue) {
            CBORSimpleValue other = (CBORSimpleValue)o;
            return this._value == other._value;
        }
        return false;
    }
}

