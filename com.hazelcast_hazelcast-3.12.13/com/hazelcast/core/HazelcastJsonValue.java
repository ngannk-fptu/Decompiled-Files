/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.util.Preconditions;

public final class HazelcastJsonValue {
    private final String string;

    public HazelcastJsonValue(String string) {
        Preconditions.checkNotNull(string);
        this.string = string;
    }

    public String toString() {
        return this.string;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HazelcastJsonValue jsonValue = (HazelcastJsonValue)o;
        return this.string != null ? this.string.equals(jsonValue.string) : jsonValue.string == null;
    }

    public int hashCode() {
        return this.string != null ? this.string.hashCode() : 0;
    }
}

