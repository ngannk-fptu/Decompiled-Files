/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.common;

import java.util.Objects;

public class Anchor {
    private final String value;

    public Anchor(String value) {
        Objects.requireNonNull(value, "Anchor must be provided.");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Empty anchor.");
        }
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Anchor anchor1 = (Anchor)o;
        return Objects.equals(this.value, anchor1.value);
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }
}

