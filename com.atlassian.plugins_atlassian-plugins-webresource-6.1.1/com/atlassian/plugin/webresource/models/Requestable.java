/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.models;

import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class Requestable {
    private final String key;

    public Requestable(@Nonnull String key) {
        this.key = Objects.requireNonNull(key, "The key is mandatory for creating the requestable.");
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Requestable) {
            Requestable otherRequestable = (Requestable)other;
            return Objects.equals(this.key, otherRequestable.key);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.key);
    }

    public String getKey() {
        return this.key;
    }

    @Deprecated
    public String toLooseType() {
        return this.key;
    }
}

