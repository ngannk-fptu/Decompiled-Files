/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import java.io.Serializable;

public class PairType
implements Serializable {
    private Serializable key;
    private Serializable value;

    public PairType() {
    }

    public PairType(Serializable key, Serializable value) {
        this.key = key;
        this.value = value;
    }

    public Serializable getKey() {
        return this.key;
    }

    public void setKey(Serializable key) {
        this.key = key;
    }

    public Serializable getValue() {
        return this.value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

    public String toString() {
        return this.key + "/" + this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PairType)) {
            return false;
        }
        PairType pairType = (PairType)o;
        if (!this.key.equals(pairType.key)) {
            return false;
        }
        return this.value.equals(pairType.value);
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 29 * result + this.value.hashCode();
        return result;
    }
}

