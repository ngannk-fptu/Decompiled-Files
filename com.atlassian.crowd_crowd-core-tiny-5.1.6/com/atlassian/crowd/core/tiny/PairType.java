/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.core.tiny;

import java.io.Serializable;

public final class PairType
implements Serializable {
    private static final long serialVersionUID = 4970043384204083681L;
    private final Serializable key;
    private final String value;

    public PairType(Serializable key, String value) {
        this.key = key;
        this.value = value;
    }

    public Serializable getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
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
        return this.key.equals(pairType.key) && this.value.equals(pairType.value);
    }

    public int hashCode() {
        return 29 * this.key.hashCode() + this.value.hashCode();
    }
}

